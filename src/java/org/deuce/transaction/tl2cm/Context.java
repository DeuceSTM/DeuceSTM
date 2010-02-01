package org.deuce.transaction.tl2cm;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.deuce.transaction.TransactionException;
import org.deuce.transaction.tl2.WriteSet;
import org.deuce.transaction.tl2.field.BooleanWriteFieldAccess;
import org.deuce.transaction.tl2.field.ByteWriteFieldAccess;
import org.deuce.transaction.tl2.field.CharWriteFieldAccess;
import org.deuce.transaction.tl2.field.DoubleWriteFieldAccess;
import org.deuce.transaction.tl2.field.FloatWriteFieldAccess;
import org.deuce.transaction.tl2.field.IntWriteFieldAccess;
import org.deuce.transaction.tl2.field.LongWriteFieldAccess;
import org.deuce.transaction.tl2.field.ObjectWriteFieldAccess;
import org.deuce.transaction.tl2.field.ReadFieldAccess;
import org.deuce.transaction.tl2.field.ShortWriteFieldAccess;
import org.deuce.transaction.tl2.field.WriteFieldAccess;
import org.deuce.transaction.tl2.pool.Pool;
import org.deuce.transaction.tl2.pool.ResourceFactory;
import org.deuce.transaction.tl2cm.Statistics.AbortType;
import org.deuce.transaction.tl2cm.WriteSetIterator.WriteSetIteratorElement;
import org.deuce.transaction.tl2cm.cm.ContentionManager;
import org.deuce.transaction.tl2cm.cm.ContentionManager.Action;
import org.deuce.transform.Exclude;

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
	public static final String TL2CM_LOGGER = "org.deuce.transaction.tl2cm";
	public static final TransactionException FAILURE_EXCEPTION = new TransactionException( "Transaction failed");
	
	//private static final Logger logger = Logger.getLogger(TL2CM_LOGGER);
	private static final AtomicInteger clock = new AtomicInteger(0);
	private static final AtomicInteger threadIdCounter = new AtomicInteger(1);
	private static final Map<Integer, Context> threads = new ConcurrentHashMap<Integer, Context>();
	private static final ContentionManager cm = Factory.createContentionManager();

	// Instance members - specific to each thread
	private final int threadId = threadIdCounter.getAndIncrement();
	private final Statistics stats = new Statistics(threadId);
	private final ReadSet readSet = new ReadSet();
	private final WriteSet writeSet = new WriteSet();
	private final AtomicInteger localClock;
	private final AtomicReference<Phase> phase;
	private final AtomicInteger priority;
	private final AtomicInteger timestamp;
	private int atomicBlockId;
	private int versionOfLastReadLock;

	// Static initialization
	static {
		try {
			System.out.println("TL2CM Initialized:");
			System.out.println("Contention Manager: " + cm.getDescription());
		} 
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public Context() {
		this.localClock = new AtomicInteger(0);
		this.priority = new AtomicInteger(0);
		this.timestamp = new AtomicInteger(0);
		this.phase = new AtomicReference<Phase>(Phase.COMMITTED);
		threads.put(threadId, this);
	}
	
	@Override
	protected void finalize() throws Throwable {
		this.phase.set(Phase.INACTIVE);
		super.finalize();
	}
	
	public void init(int atomicBlockId, String metainf) {
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
		int currentClock = clock.get();
		this.localClock.set(currentClock);
		if (cm.requiresTimestamps() && phase.get().equals(Phase.COMMITTED)) {
			// Only if the last transaction committed we issue a new timestamp
			// otherwise we continue with the old one 
			this.timestamp.set(currentClock);
		} 
		
		this.atomicBlockId = atomicBlockId;
		this.phase.set(Phase.RUNNING);
		this.stats.reportTxStart();
	}

	public boolean commit() {
		this.stats.reportOnCommit(readSet.size(), writeSet.size());

		// Read-only transactions don't have to do anything in order to commit
		if (writeSet.isEmpty()) {
			this.stats.reportCommit();
			return true;
		}
		
		// Writing transaction have to go through a different algorithm
		List<WriteSetIteratorElement> lockedList = lockWriteSet();
		if (lockedList.size() == writeSet.size()) {
			boolean readSetValidated = readSet.isConsistent(localClock.get());
			if (readSetValidated) {
				boolean committed = phase.compareAndSet(Phase.RUNNING, Phase.COMMITTED);
				if (committed) {
					// Get a new version number
					int newClock = clock.incrementAndGet();
					// Write values to memory
					for (WriteFieldAccess writeField : writeSet) {
						writeField.put(); 
					}
					// Update and release locks
					for (WriteFieldAccess writeField : writeSet) {
						LockTable.updateAndUnlock(writeField.hashCode(), newClock);
					}
					if (cm.requiresPriorities()) {
						priority.set(0);
					}
					this.stats.reportCommit();
					return true;
				}
			}
			else {
				this.stats.reportAbort(AbortType.COMMIT_READSET_VALIDATION);
			}
		}
		else {
			this.stats.reportWriteSetValidationFailureDuringCommit(lockedList.size()+1);
			this.stats.reportAbort(AbortType.COMMIT_WRITESET_LOCKING);
		}
		
		// Commit did not succeed, change phase and unlock the locks I did acquire
		this.phase.set(Phase.ABORTED);
		for (WriteSetIteratorElement elem : lockedList) {
			int hash = elem.getField().hashCode();
			LockTable.unLock(hash, threadId, -1);
		}
		return false;
	}

	public void rollback() {

	}
	
	/**
	 * Kills this transaction if it is still running
	 * @return true if the kill operation succeeded, false otherwise
	 */
	public boolean kill() {
		return phase.compareAndSet(Phase.RUNNING, Phase.ABORTED);
	}
	
	/**
	 * Gets the Id of this thread
	 * @return Id of this thread
	 */
	public int getThreadId() {
		return threadId;
	}

	/**
	 * Returns the phase of this transaction
	 * @return phase of this transaction
	 */
	public Phase getPhase() {
		return this.phase.get();
	}
	
	/**
	 * Gets the clock value of the current transaction
	 * @return clock value of the current transaction
	 */
	public int getLocalClock() {
		return localClock.get();
	}
	
	/**
	 * Gets the timestamp of the current transaction. The timestamp
	 * of the current transaction is the local clock value of the 
	 * first attempt made by this thread to execute the current 
	 * transaction. 
	 * @return timestamp of the current transaction
	 */
	public int getTimestamp() {
		return timestamp.get();
	}

	/**
	 * Gets the priority of this thread. The priority
	 * is calculated based on the number of locks this
	 * thread has opened and not committed on.
	 * @return thread's priority
	 */
	public int getPriority() {
		return priority.get();
	}

	public Statistics getStatistics() {
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
		sb.append(localClock);
		sb.append(", globalClock=");
		sb.append(clock.get());
		sb.append(" writeSet=");
		sb.append(writeSet.size());
		if (cm.requiresPriorities()) {
			sb.append(", priority=");
			sb.append(priority.get());
		}
		String str = sb.toString();
		return str;
	}

	private List<WriteSetIteratorElement> lockWriteSet() {
		boolean killedByCM = false;
		WriteSetIterator wsIter = new WriteSetIterator(writeSet);
		while (!killedByCM && !wsIter.isEmpty() && phase.get().equals(Phase.RUNNING)) {
			WriteFieldAccess writeField = wsIter.getLock();
			// Attempt to acquire this lock as long as we did not get a command to restart/skip by 
			// a contention manager or some other thread killed us
			while (!killedByCM && phase.get().equals(Phase.RUNNING)) {
				boolean lockedByForce = false;
				long lock = LockTable.lock(writeField.hashCode(), threadId, localClock.get());
				int lockOwner = LockTable.getOwner(lock);
				if (lockOwner == -1) {
					// Lock appeared to be free, but when I tried to CAS it, I failed. The owner of the
					// lock is not known to me, therefore I can't efficiently do contention management, so I just retry.
					continue;
				}
				else if (threadId != lockOwner) {	
					// Lock is owned by some other thread. Activating Contention Management
					Context otherCtx = threads.get(lockOwner);
					if (otherCtx.getPhase().equals(Phase.INACTIVE)) {
						// This can happen if the other thread will no longer run transactions, but I 
						// conflicted with it (before it changes it's phase to INACTIVE). The best thing to do
						// is to retry the lock in hopes no one else locked it in the meantime.
						continue;
					}
					else {
						Action action = cm.resolve(writeField, this, otherCtx);
						if (action.equals(Action.RESTART)) {
							// this will cause my transaction to roll-back and restart itself
							killedByCM = true;
							break;
						}
						else if (action.equals(Action.RETRY_LOCK)) {
							continue;
						}
						else if (action.equals(Action.STEAL_LOCK)) {
							int versionForStealing = clock.incrementAndGet();
							lockedByForce = LockTable.forceLock(writeField.hashCode(), lock, threadId, versionForStealing);
						}
						else if (action.equals(Action.SKIP_LOCK)) {
							wsIter.skipLock();
							continue;
						}
					}
				}
				if (threadId == lockOwner || lockedByForce){
					// Lock is acquired!
					//trace("Lock #{0} acquired", new Object[]{lockedCounter});
					if (cm.requiresPriorities()) {
						priority.incrementAndGet();
					}
					wsIter.lockAcquired(lockedByForce);
					break;	// continue to next lock
				}
			}
		}
		return wsIter.getAcquiredLocks();
	} 

	
	private WriteFieldAccess onReadAccess0(Object obj, long field) {
		ReadFieldAccess current = readSet.getCurrent();
		int hash = current.hashCode();
		long lock = LockTable.getLock(hash);
		int version = LockTable.getVersion(lock);
		
		// We want to make sure the lock hasn't changed
		// to be sure we read a consistent value from memory 
		if (version != versionOfLastReadLock) {
			stats.reportAbort(AbortType.SPECULATION_READVERSION);
			throw FAILURE_EXCEPTION;
		}
		if (LockTable.isLocked(lock)) {
			stats.reportAbort(AbortType.SPECULATION_LOCATION_LOCKED);
			throw FAILURE_EXCEPTION;
		}
		
		// Check if the location is already included in the write set
		return writeSet.contains(current);
	}

	private void addWriteAccess0(WriteFieldAccess write) {
		//trace("adding write to hash={0}", new Object[]{write.hashCode()});
		writeSet.put(write);
	}

//	private void trace(String message, Object[] params) {
//		StringBuilder sb = new StringBuilder(message);
//		sb.append(this.toString());
//		String str = sb.toString();
//		logger.log(Level.INFO, str, params);
//	}

	public void beforeReadAccess(Object obj, long field) {
		ReadFieldAccess next = readSet.getNext();
		next.init(obj, field);
		// Check that the location's version is consistent with 
		// localClock. If not, throw an exception
		int hash = next.hashCode();
		long lock = LockTable.getLock(hash);
		versionOfLastReadLock = LockTable.getVersion(lock);
		if (versionOfLastReadLock > localClock.get()) {
			stats.reportAbort(AbortType.SPECULATION_READVERSION);
			throw FAILURE_EXCEPTION;
		}
		// The version of the location is consistent
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

	private Pool<ObjectWriteFieldAccess> objectPool = new Pool<ObjectWriteFieldAccess>(
			new ResourceFactory<ObjectWriteFieldAccess>() {
				public ObjectWriteFieldAccess newInstance() {
					return new ObjectWriteFieldAccess();
				}
			});

	private Pool<BooleanWriteFieldAccess> booleanPool = new Pool<BooleanWriteFieldAccess>(
			new ResourceFactory<BooleanWriteFieldAccess>() {
				public BooleanWriteFieldAccess newInstance() {
					return new BooleanWriteFieldAccess();
				}
			});

	private Pool<ByteWriteFieldAccess> bytePool = new Pool<ByteWriteFieldAccess>(
			new ResourceFactory<ByteWriteFieldAccess>() {
				public ByteWriteFieldAccess newInstance() {
					return new ByteWriteFieldAccess();
				}
			});

	private Pool<CharWriteFieldAccess> charPool = new Pool<CharWriteFieldAccess>(
			new ResourceFactory<CharWriteFieldAccess>() {
				public CharWriteFieldAccess newInstance() {
					return new CharWriteFieldAccess();
				}
			});

	private Pool<ShortWriteFieldAccess> shortPool = new Pool<ShortWriteFieldAccess>(
			new ResourceFactory<ShortWriteFieldAccess>() {
				public ShortWriteFieldAccess newInstance() {
					return new ShortWriteFieldAccess();
				}
			});

	private Pool<IntWriteFieldAccess> intPool = new Pool<IntWriteFieldAccess>(
			new ResourceFactory<IntWriteFieldAccess>() {
				public IntWriteFieldAccess newInstance() {
					return new IntWriteFieldAccess();
				}
			});

	private Pool<LongWriteFieldAccess> longPool = new Pool<LongWriteFieldAccess>(
			new ResourceFactory<LongWriteFieldAccess>() {
				public LongWriteFieldAccess newInstance() {
					return new LongWriteFieldAccess();
				}
			});

	private Pool<FloatWriteFieldAccess> floatPool = new Pool<FloatWriteFieldAccess>(
			new ResourceFactory<FloatWriteFieldAccess>() {
				public FloatWriteFieldAccess newInstance() {
					return new FloatWriteFieldAccess();
				}
			});

	private Pool<DoubleWriteFieldAccess> doublePool = new Pool<DoubleWriteFieldAccess>(
			new ResourceFactory<DoubleWriteFieldAccess>() {
				public DoubleWriteFieldAccess newInstance() {
					return new DoubleWriteFieldAccess();
				}
			});

}
