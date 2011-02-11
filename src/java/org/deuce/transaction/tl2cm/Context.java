package org.deuce.transaction.tl2cm;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.deuce.transaction.TransactionException;
import org.deuce.transaction.tl2.pool.Pool;
import org.deuce.transaction.tl2.pool.ResourceFactory;
import org.deuce.transaction.tl2cm.Statistics.AbortType;
import org.deuce.transaction.tl2cm.cm.ContentionManager;
import org.deuce.transaction.tl2cm.cm.ContentionManager.Action;
import org.deuce.transaction.tl2cm.field.BooleanWriteFieldAccess;
import org.deuce.transaction.tl2cm.field.ByteWriteFieldAccess;
import org.deuce.transaction.tl2cm.field.CharWriteFieldAccess;
import org.deuce.transaction.tl2cm.field.DoubleWriteFieldAccess;
import org.deuce.transaction.tl2cm.field.FloatWriteFieldAccess;
import org.deuce.transaction.tl2cm.field.IntWriteFieldAccess;
import org.deuce.transaction.tl2cm.field.LongWriteFieldAccess;
import org.deuce.transaction.tl2cm.field.ObjectWriteFieldAccess;
import org.deuce.transaction.tl2cm.field.ReadFieldAccess;
import org.deuce.transaction.tl2cm.field.ShortWriteFieldAccess;
import org.deuce.transaction.tl2cm.field.WriteFieldAccess;
import org.deuce.transform.Exclude;
import org.deuce.trove.TObjectProcedure;

/**
 * Context class for TL2 STM with Contention Management support. This class queries its 
 * Contention Manager regarding the need for bookkeeping of shared information, like
 * priorities, currently running transaction, etc. 
 * 
 * Based on Guy Korland's and Pascal Felber's work on <code>org.deuce.transaction.tl2.*</code> 
 * and <code>org.deuce.transaction.lsa*</code>
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 */
@Exclude
final public class Context implements org.deuce.transaction.Context {

	// Static members - shared by all threads
	public static final int TX_RUNNING = 0;
	public static final int TX_COMMITTED = 1;
	public static final int TX_ABORTED = 2;
	public static final TransactionException FAILURE_EXCEPTION = new TransactionException("Transaction failed");

	private static final AtomicInteger globalClock = new AtomicInteger(0);
	private static final AtomicInteger threadIdCounter = new AtomicInteger(1);
	private static final Context[] threads = new Context[256];
	
	//Global lock used to allow only one irrevocable transaction solely. 
	final private static ReentrantReadWriteLock irrevocableAccessLock = new ReentrantReadWriteLock();
	private boolean irrevocableState = false;
	
	// Instance members - specific to each thread
	private final ContentionManager cm = Factory.createContentionManager();
	private final ReadSet readSet = new ReadSet();
	private final WriteSet writeSet = new WriteSet();
	private final AtomicInteger statusRecord = new AtomicInteger(0);
	private final AtomicInteger karma = new AtomicInteger(0);
	private final AtomicInteger killKarma = new AtomicInteger(0);
	private final UpdateAndUnlockProcedure updateAndUnlockProcedure = new UpdateAndUnlockProcedure();
	private final TObjectProcedure<WriteFieldAccess> putProcedure = new TObjectProcedure<WriteFieldAccess>(){
		@Override
		public boolean execute(WriteFieldAccess writeField) {
			writeField.put();
			return true;
		}
	};
	private final int threadId;
	private int rv;
	private int atomicBlockId;
	private int lastReadLockVersion;
	private int localClock;
	private int attempts;
	private final Statistics stats;	
	
	// Static initialization
	static {
		try {
			System.out.println("TL2CM Initialized:");
			System.out.println("Contention Manager: " + Factory.createContentionManager().getDescription());
		} 
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public Context() {
		threadId = threadIdCounter.getAndIncrement();
		int maxThreads = (int) Math.pow(2, LockTable.OWNERSIZE);
		if (threadId > maxThreads) {
			System.err.println("Too many threads in the system. Max number of threads allowed is " + maxThreads);
			System.exit(1);
		}
		stats = new Statistics(threadId);
		this.localClock = 0;
		threads[threadId] = this;
	}
	
	public final void init(int atomicBlockId, String metainf) {
		this.atomicBlockId = atomicBlockId;
		this.cm.init();
		this.readSet.clear();
		this.writeSet.clear();
		this.objectPool.clear();
		this.booleanPool.clear();
		this.bytePool.clear();
		this.charPool.clear();
		this.shortPool.clear();
		this.intPool.clear();
		this.longPool.clear();
		this.floatPool.clear();
		this.doublePool.clear();
		
		//Lock according to the transaction irrevocable state
		if(irrevocableState)
			irrevocableAccessLock.writeLock().lock();
		else
			irrevocableAccessLock.readLock().lock();
		
		this.rv = globalClock.get();
		this.attempts++;
		
		// Reset localClock if max value reached
		if (localClock < (1 << LockTable.OWNERCLOCK_SIZE)) {
			this.localClock++;
		}
		else {
			this.localClock = 0;
		}
		int statusRecord = generateStatusRecord(TX_RUNNING, localClock);
		this.statusRecord.set(statusRecord);
		this.stats.reportTxStart();
	}

	public final boolean commit() {
		try{
			this.stats.reportOnCommit(readSet.size(), writeSet.size());
			// Read-only transactions don't have to do anything in order to commit
			if (writeSet.isEmpty()) {
				this.stats.reportCommit(attempts);
				resetPriorities();
				return true;
			}

			// Writing transactions have to go through a different algorithm
			int lockedCounter = lockWriteSet();
			if (lockedCounter == writeSet.size()) {
				boolean readSetValidated = readSet.validate(rv);
				if (readSetValidated) {
					int expectedStatusRecord = generateStatusRecord(TX_RUNNING, localClock);
					int newStatusRecord = generateStatusRecord(TX_COMMITTED, localClock);
					boolean committed = statusRecord.compareAndSet(expectedStatusRecord, newStatusRecord);
					if (committed) {
						// Get a new version number
						int newClock = globalClock.incrementAndGet();

						// Write values to memory
						writeSet.forEach(putProcedure);

						// Update and release locks
						updateAndUnlockProcedure.setNewClock(newClock);
						writeSet.forEach(updateAndUnlockProcedure);

						this.stats.reportCommit(attempts);
						resetPriorities();
						return true;
					}
					else {
						this.stats.reportAbort(AbortType.COMMIT_KILLED); 
					}
				}
				else {
					this.stats.reportAbort(AbortType.COMMIT_READSET_VALIDATION); 
				}
			}
			else {
				this.stats.reportWriteSetValidationFailureDuringCommit(lockedCounter+1);
				this.stats.reportAbort(AbortType.COMMIT_WRITESET_LOCKING);
			}

			// Commit did not succeed, roll-back all the changes
			Iterator<WriteFieldAccess> iter = writeSet.iterator();
			while (lockedCounter > 0) {
				WriteFieldAccess field = iter.next();
				int hash = field.hashCode();
				LockTable.unLock(hash, threadId);
				lockedCounter--;
			}
			return false;

		}
		finally{
			if(irrevocableState){
				irrevocableState = false;
				irrevocableAccessLock.writeLock().unlock();
			}
			else{
				irrevocableAccessLock.readLock().unlock();
			}

		}
	}

	public final void rollback() {
		irrevocableAccessLock.readLock().unlock();
	}
	
	public final boolean kill(int clockValue) {
		if (clockValue == -1) {
			// The transaction killed itself. No need to CAS - if someone else killed
			// me before I can just override the variable
			int newStatusRecord = generateStatusRecord(TX_ABORTED, 0);
			statusRecord.set(newStatusRecord);
			return true;
		}
		else {
			int expectedStatusRecord = generateStatusRecord(TX_RUNNING, clockValue);
			int newStatusRecord = generateStatusRecord(TX_ABORTED, clockValue);
			return statusRecord.compareAndSet(expectedStatusRecord, newStatusRecord);
		}
	}
	
	/**
	 * Gets the Id of this thread
	 * @return Id of this thread
	 */
	public final int getThreadId() {
		return threadId;
	}

	/**
	 * Gets the priority of this thread. The priority
	 * is calculated based on the number of locks this
	 * thread has opened and not committed on.
	 * @return thread's priority
	 */
	public final int getPriority() {
		return karma.get();
	}

	public final void changePriority(int delta) {
		karma.addAndGet(delta);
	}
	
	public final int getKillPriority() {
		return killKarma.get();
	}

	public final void changeKillPriority(int delta) {
		killKarma.addAndGet(delta);
	}
	
	public final Statistics getStatistics() {
		return stats;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" TID=");
		sb.append(threadId);
		sb.append(", TX=");
		sb.append(atomicBlockId);
		sb.append(", localClock=");
		sb.append(rv);
		sb.append(", globalClock=");
		sb.append(globalClock.get());
		sb.append(" writeSet=");
		sb.append(writeSet.size());
		sb.append(", prio=");
		sb.append(getPriority());
		String str = sb.toString();
		return str;
	}

	private final void resetPriorities() {
		if (cm.requiresPriorities()) {
			karma.set(0);
		}
		if (cm.requiresKillPriorities()) {
			killKarma.set(0);
		}
		attempts = 0;
	}
	
	private final boolean isStillAlive() {
		int statusRecord = this.statusRecord.get();
		return getTxStatus(statusRecord) == TX_RUNNING;
	}
	
	private final int lockWriteSet() {
		int lockedCounter = 0;
		boolean killedByCM = false;
		Iterator<WriteFieldAccess> iter = writeSet.iterator();
		while (!killedByCM && iter.hasNext() && isStillAlive()) {
			WriteFieldAccess writeField = iter.next();
			// Attempt to acquire this lock as long as we did not get a command to restart/skip by 
			// a contention manager or some other thread killed us
			while (!killedByCM && isStillAlive()) {
				boolean lockedByForce = false;
				int hash = writeField.hashCode();
				long[] res = LockTable.lock(hash, threadId, localClock);
				long originalLock = res[1];
				if (res[0] == 2) {
					// Lock appeared to be free, but when I tried to CAS it, I failed. The owner of the
					// lock is not known to me, therefore I can't efficiently do contention management, so I just retry.
					continue;
				}
				else if (res[0] == 1) {
					// Lock was not acquired, but the owner of the lock is known
					int lockOwner = LockTable.getOwner(originalLock);
					Context otherCtx = threads[lockOwner];
					Action action = cm.resolveWriteConflict(writeField, this, otherCtx);
					if (action.equals(Action.RESTART)) {
						killedByCM = true;
						break;
					}
					else if (action.equals(Action.RETRY)) {
						continue;
					}
					else if (action.equals(Action.STEAL_LOCK)) {
						int version = LockTable.getVersion(originalLock);
						lockedByForce = LockTable.forceLock(hash, threadId, localClock, version, originalLock);
						if (!lockedByForce) {
							res = LockTable.lock(hash, threadId, localClock);
						}
					}
				}
				
				if (res[0] == 0 || lockedByForce){
					lockedCounter++;
					if (cm.requiresPriorities()) {
						karma.addAndGet(10);
					}
					break;	// continue to next lock
				}
			}
		}
		return lockedCounter;
	} 

	
	private final WriteFieldAccess onReadAccess0(Object obj, long field) {
		ReadFieldAccess current = readSet.getCurrent();
		int hash = current.hashCode();
		long lock = LockTable.getLock(hash);
		// We want to make sure the lock hasn't changed
		// to be sure we read a consistent value from memory 
		while (LockTable.isLocked(lock)) {
			int lockOwner = LockTable.getOwner(lock);
			Context ownerCtx = threads[lockOwner];
			Action action = cm.resolveReadConflict(current, this, ownerCtx);
			if (action.equals(Action.RESTART)) {
				stats.reportAbort(AbortType.SPECULATION_LOCATION_LOCKED);
				throw FAILURE_EXCEPTION;
			}
			if (action.equals(Action.RETRY)) {
				lock = LockTable.getLock(hash);
				continue;
			}
			if (action.equals(Action.CONTINUE)) {
				break;
			}
		}
		int version = LockTable.getVersion(lock);
		if (version > lastReadLockVersion) {
			stats.reportAbort(AbortType.SPECULATION_READVERSION);
			throw FAILURE_EXCEPTION;
		}
		if (cm.requiresPriorities()) {
			karma.incrementAndGet();
		}
		cm.init();
		return writeSet.contains(current);
	}

	private final void addWriteAccess0(WriteFieldAccess write) {
		writeSet.put(write);
	}

	private static final int generateStatusRecord(int status, int clock) {
		int statusRecord = (status << LockTable.OWNERCLOCK_SIZE) | clock;
		return statusRecord;
	}
	
	public static final int getTxStatus(int statusRecord) {
		int status = statusRecord >> LockTable.OWNERCLOCK_SIZE;
		return status;
	}
	
	public static final int getTxLocalClock(int statusRecord) {
		return statusRecord & ((1<<LockTable.OWNERCLOCK_SIZE)-1);
	}
	
	public final int getStatusRecord() {
		return this.statusRecord.get();
	}
	
	public final void beforeReadAccess(Object obj, long field) {
		ReadFieldAccess current = readSet.getNext();
		current.init(obj, field);
		// Check that the location's version is consistent with 
		// localClock. If not, throw an exception
		int hash = current.hashCode();
		long lock = LockTable.getLock(hash);
		lastReadLockVersion = LockTable.getVersion(lock);
		if (lastReadLockVersion > rv) {
			stats.reportAbort(AbortType.SPECULATION_READVERSION);
			throw FAILURE_EXCEPTION;
		}
	}

	public Object onReadAccess(Object obj, Object value, long field) {
		WriteFieldAccess writeAccess = onReadAccess0(obj, field);
		if (writeAccess == null)
			return value;

		return ((ObjectWriteFieldAccess) writeAccess).getValue();
	}

	public boolean onReadAccess(Object obj, boolean value, long field) {
		WriteFieldAccess writeAccess = onReadAccess0(obj, field);
		if (writeAccess == null)
			return value;

		return ((BooleanWriteFieldAccess) writeAccess).getValue();
	}

	public byte onReadAccess(Object obj, byte value, long field) {
		WriteFieldAccess writeAccess = onReadAccess0(obj, field);
		if (writeAccess == null)
			return value;

		return ((ByteWriteFieldAccess) writeAccess).getValue();
	}

	public char onReadAccess(Object obj, char value, long field) {
		WriteFieldAccess writeAccess = onReadAccess0(obj, field);
		if (writeAccess == null)
			return value;

		return ((CharWriteFieldAccess) writeAccess).getValue();
	}

	public short onReadAccess(Object obj, short value, long field) {
		WriteFieldAccess writeAccess = onReadAccess0(obj, field);
		if (writeAccess == null)
			return value;

		return ((ShortWriteFieldAccess) writeAccess).getValue();

	}

	public int onReadAccess(Object obj, int value, long field) {
		WriteFieldAccess writeAccess = onReadAccess0(obj, field);
		if (writeAccess == null)
			return value;

		return ((IntWriteFieldAccess) writeAccess).getValue();
	}

	public long onReadAccess(Object obj, long value, long field) {
		WriteFieldAccess writeAccess = onReadAccess0(obj, field);
		if (writeAccess == null)
			return value;

		return ((LongWriteFieldAccess) writeAccess).getValue();
	}

	public float onReadAccess(Object obj, float value, long field) {
		WriteFieldAccess writeAccess = onReadAccess0(obj, field);
		if (writeAccess == null)
			return value;

		return ((FloatWriteFieldAccess) writeAccess).getValue();
	}

	public double onReadAccess(Object obj, double value, long field) {
		WriteFieldAccess writeAccess = onReadAccess0(obj, field);
		if (writeAccess == null)
			return value;

		return ((DoubleWriteFieldAccess) writeAccess).getValue();
	}

	public void onWriteAccess(Object obj, Object value, long field) {
		ObjectWriteFieldAccess next = objectPool.getNext();
		next.set(value, obj, field);
		addWriteAccess0(next);
	}

	public void onWriteAccess(Object obj, boolean value, long field) {

		BooleanWriteFieldAccess next = booleanPool.getNext();
		next.set(value, obj, field);
		addWriteAccess0(next);
	}

	public void onWriteAccess(Object obj, byte value, long field) {

		ByteWriteFieldAccess next = bytePool.getNext();
		next.set(value, obj, field);
		addWriteAccess0(next);
	}

	public void onWriteAccess(Object obj, char value, long field) {

		CharWriteFieldAccess next = charPool.getNext();
		next.set(value, obj, field);
		addWriteAccess0(next);
	}

	public void onWriteAccess(Object obj, short value, long field) {

		ShortWriteFieldAccess next = shortPool.getNext();
		next.set(value, obj, field);
		addWriteAccess0(next);
	}

	public void onWriteAccess(Object obj, int value, long field) {

		IntWriteFieldAccess next = intPool.getNext();
		next.set(value, obj, field);
		addWriteAccess0(next);
	}

	public void onWriteAccess(Object obj, long value, long field) {

		LongWriteFieldAccess next = longPool.getNext();
		next.set(value, obj, field);
		addWriteAccess0(next);
	}

	public void onWriteAccess(Object obj, float value, long field) {

		FloatWriteFieldAccess next = floatPool.getNext();
		next.set(value, obj, field);
		addWriteAccess0(next);
	}

	public void onWriteAccess(Object obj, double value, long field) {

		DoubleWriteFieldAccess next = doublePool.getNext();
		next.set(value, obj, field);
		addWriteAccess0(next);
	}

	private class UpdateAndUnlockProcedure implements TObjectProcedure<WriteFieldAccess>{
	
		private int newClock;
	
		@Override
		public boolean execute(WriteFieldAccess writeField) {
			LockTable.updateAndUnlock(writeField.hashCode(), newClock);
			return true;
		}
		
		
		public void setNewClock(int newClock){
			this.newClock = newClock;
		}
	}
	
	private static class ObjectResourceFactory implements ResourceFactory<ObjectWriteFieldAccess>{
		@Override
		public ObjectWriteFieldAccess newInstance() {
			return new ObjectWriteFieldAccess();
		}
	}
	final private Pool<ObjectWriteFieldAccess> objectPool = new Pool<ObjectWriteFieldAccess>(new ObjectResourceFactory());

	private static class BooleanResourceFactory implements ResourceFactory<BooleanWriteFieldAccess>{
		@Override
		public BooleanWriteFieldAccess newInstance() {
			return new BooleanWriteFieldAccess();
		}
	}
	final private Pool<BooleanWriteFieldAccess> booleanPool = new Pool<BooleanWriteFieldAccess>(new BooleanResourceFactory());

	private static class ByteResourceFactory implements ResourceFactory<ByteWriteFieldAccess>{
		@Override
		public ByteWriteFieldAccess newInstance() {
			return new ByteWriteFieldAccess();
		}
	}
	final private Pool<ByteWriteFieldAccess> bytePool = new Pool<ByteWriteFieldAccess>( new ByteResourceFactory());

	private static class CharResourceFactory implements ResourceFactory<CharWriteFieldAccess>{
		@Override
		public CharWriteFieldAccess newInstance() {
			return new CharWriteFieldAccess();
		}
	}
	final private Pool<CharWriteFieldAccess> charPool = new Pool<CharWriteFieldAccess>(new CharResourceFactory());

	private static class ShortResourceFactory implements ResourceFactory<ShortWriteFieldAccess>{
		@Override
		public ShortWriteFieldAccess newInstance() {
			return new ShortWriteFieldAccess();
		}
	}
	final private Pool<ShortWriteFieldAccess> shortPool = new Pool<ShortWriteFieldAccess>( new ShortResourceFactory());

	private static class IntResourceFactory implements ResourceFactory<IntWriteFieldAccess>{
		@Override
		public IntWriteFieldAccess newInstance() {
			return new IntWriteFieldAccess();
		}
	}
	final private Pool<IntWriteFieldAccess> intPool = new Pool<IntWriteFieldAccess>( new IntResourceFactory());

	private static class LongResourceFactory implements ResourceFactory<LongWriteFieldAccess>{
		@Override
		public LongWriteFieldAccess newInstance() {
			return new LongWriteFieldAccess();
		}
	}
	final private Pool<LongWriteFieldAccess> longPool = new Pool<LongWriteFieldAccess>( new LongResourceFactory());
	
	private static class FloatResourceFactory implements ResourceFactory<FloatWriteFieldAccess>{
		@Override
		public FloatWriteFieldAccess newInstance() {
			return new FloatWriteFieldAccess();
		}
	}
	final private Pool<FloatWriteFieldAccess> floatPool = new Pool<FloatWriteFieldAccess>( new FloatResourceFactory());
	
	private static class DoubleResourceFactory implements ResourceFactory<DoubleWriteFieldAccess>{
		@Override
		public DoubleWriteFieldAccess newInstance() {
			return new DoubleWriteFieldAccess();
		}
	}
	final private Pool<DoubleWriteFieldAccess> doublePool = new Pool<DoubleWriteFieldAccess>( new DoubleResourceFactory());
	
	@Override
	public void onIrrevocableAccess() {
		if(irrevocableState) // already in irrevocable state so no need to restart transaction.
			return;

		irrevocableState = true;
		throw TransactionException.STATIC_TRANSACTION;
	}
}
