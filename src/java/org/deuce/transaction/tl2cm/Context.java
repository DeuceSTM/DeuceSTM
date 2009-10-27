package org.deuce.transaction.tl2cm;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.deuce.transaction.TransactionException;
import org.deuce.transaction.tl2cm.cm.ContentionManager;
import org.deuce.transaction.tl2cm.cm.ContentionManager.Action;
import org.deuce.transaction.tl2cm.contexts.ContextsMap;
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

	public static final String TL2CM_LOGGER = "org.deuce.transaction.tl2cm";
	//private static final Logger logger = Logger.getLogger(TL2CM_LOGGER);
	//private static final boolean isTracing = logger.isLoggable(Level.INFO);
	private static final AtomicInteger clock = new AtomicInteger(0);
	private static final AtomicInteger threadIdCounter = new AtomicInteger(LockTable.INITIAL_OWNER+1);
	private static final ContextsMap threads = Factory.createContextsMap();
	private static ContentionManager cm;
	
	private final int threadId;
	private final ReadSet readSet = new ReadSet();
	private final WriteSet writeSet = new WriteSet();
	private final Statistics stats = new Statistics();
	private final AtomicInteger localClock;
	private final AtomicReference<Status> status;
	private final AtomicInteger priority = new AtomicInteger(0);
	private final AtomicInteger timestamp = new AtomicInteger(0);
	private int atomicBlockId;
	private long lastReadLock;
	
	static {
		try {
			System.out.println("TL2CM Initialized:");
			cm = Factory.createContentionManager();
			System.out.println("Contention Manager: " + cm.getClass().getSimpleName() + "\n");
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public Context() {
		this.threadId = threadIdCounter.getAndIncrement();
		this.localClock = new AtomicInteger(0);
		this.status = new AtomicReference<Status>(Status.COMMITTED);
		threads.put(threadId, this);
	}
	
	@Override
	protected void finalize() throws Throwable {
		 //threads.remove(threadId);
		 super.finalize();
	}
	
	public void init(int atomicBlockId) {
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
		this.atomicBlockId = atomicBlockId;
		this.localClock.set(clock.get());
		if (cm.requiresTimestamps() && status.get().equals(Status.COMMITTED)) {
			// Only if the last transaction committed we issue a new timestamp
			// otherwise we continue with the old one 
			this.timestamp.set(localClock.get());
		}
		this.status.set(Status.RUNNING);
		this.stats.starts++;
	}

	public boolean commit() {
		//trace("Commit begin", null);
		if (writeSet.isEmpty()) { 
			// if the writeSet is empty no need to lock a thing - all read locations
			// were validated before we read them
			//trace("Commit end | result = true (writeSet empty)", null);
			this.stats.commits++;
			return true;
		}
		
		// Lock write set
		int lockedCounter = 0;
		for (WriteFieldAccess writeField : writeSet) {
			//trace("trying to acquire lock #{0}", new Object[]{lockedCounter});
			while (status.get().equals(Status.RUNNING)) {
				int lockOwner = LockTable.lock(writeField.hashCode(), threadId);
				if (lockOwner == -1) {
					// Lock appeared to be free, but when I tried to CAS it, I failed.
					// The owner of the lock is not known to me, therefore I can't 
					// efficiently do contention management, so I just retry.
					continue;
				}
				else if (threadId != lockOwner) {	
					// Lock is owned by some other thread. Contention Management is in order
					Context otherCtx = threads.get(lockOwner);
					if (otherCtx == null) {
						// this can happen if the other thread already finished and 
						// un-registered its context object. In this case, I retry.
						//trace("Lock owner is not available, probably already finished. Retrying lock #{0}", new Object[]{lockedCounter});
						continue;
					}
					else {
						Action action = cm.resolve(writeField, this, otherCtx);
						if (action == Action.RESTART) {
							// this will cause my transaction to roll-back and restart itself
							//trace("restarting transaction as a result of contention on lock #{0}", new Object[]{lockedCounter});
							kill(-1);
							break;
						}
						else if (action == Action.RETRY_LOCK) {
							// try again to acquire the lock
							//trace("Retrying lock #{0}", new Object[]{lockedCounter});
							continue;
						}
					}
				}
				else {
					// Lock is acquired!
					//trace("Lock #{0} acquired", new Object[]{lockedCounter});
					if (cm.requiresPriorities()) {
						priority.incrementAndGet();
					}
					lockedCounter++;
					break;
				}
			}
			// If the while loop ended because someone killed me
			// don't go to the next lock
			if (!status.get().equals(Status.RUNNING)) {
				break;
			}
		}
		// If the write set is locked, validate read set
		boolean readSetValidated = false;
		if (status.get().equals(Status.RUNNING)) {
			try {
				readSet.checkClock(localClock.get());
				readSetValidated = true;
			}
			catch (TransactionException e) {
				//trace("Commit | read set not validated", null);
			}
		}
		// Wrap things up - try to commit
		boolean committed = false;
		if (readSetValidated) {
			// If the read set is validated we can update the locks with a new version
			int newClock = clock.incrementAndGet();
			//trace("new clock is {0}", new Object[]{newClock});
			for (WriteFieldAccess writeField : writeSet) {
				writeField.put(); // commit value to field
				LockTable.setAndReleaseLock(writeField.hashCode(), newClock, threadId);
			}
			//trace("Commit end | result = true", null);
			if (cm.requiresPriorities()) {
				priority.set(0);
			}
			committed = status.compareAndSet(Status.RUNNING, Status.COMMITTED);
			if (committed) {
				this.stats.commits++;
				return true;
			}
		}
		
		// Could not commit - rollback
		if (!readSetValidated || !committed) {
			rollback(lockedCounter);
			//trace("Commit end | result = false", null);
			return false;
		}
		return false;
	}

	public void rollback() {

	}
	
	/**
	 * Kills the transaction executed by this thread. This method only kills
	 * the transaction that caused conflict by checking that the local clock
	 * value at the time the method was invoked equals the local clock value
	 * at the time the method is run.
	 * @param timeOfKill local clock at the time the kill or -1 if this thread kills itself
	 * method was invoked
	 */
	public void kill(int timeOfKill) {
		if (timeOfKill == -1 || localClock.get() == timeOfKill) {
			//trace("kill", null);
			status.compareAndSet(Status.RUNNING, Status.ABORTED);
		}
	}
	
	/**
	 * Gets the Id of this thread
	 * @return Id of this thread
	 */
	public int getThreadId() {
		return threadId;
	}

	public Status getStatus() {
		return status.get();
	}
	
	public void setStatus(Status status) {
		this.status.set(status);
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
		return this.stats;
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

	/**
	 * Rolls back the transaction by releasing the locations acquired 
	 * so far during the <code>commit</code> method
	 * @param locksCounter amount of locked locations
	 */
	private void rollback(int locksCounter) {
		for (WriteFieldAccess writeField : writeSet) {
			if (locksCounter-- > 0) {
				LockTable.unLock(writeField.hashCode());
			}
		}
	}
	
//	private void trace(String message, Object[] params) {
//		if (isTracing) {
//			StringBuilder sb = new StringBuilder(message);
//			sb.append(this.toString());
//			String str = sb.toString();
//			logger.log(Level.INFO, str, params);
//		}
//	}

	private WriteFieldAccess onReadAccess0(Object obj, long field) {
		ReadFieldAccess current = readSet.getCurrent();
		int hash = current.hashCode();
		// Check the read is still valid
		LockTable.checkLock(hash, localClock.get(), lastReadLock);
		// Check if it is already included in the write set
		return writeSet.contains(current);
	}

	private void addWriteAccess0(WriteFieldAccess write) {
		//trace("adding write to hash={0}", new Object[]{write.hashCode()});
		// Add to write set
		writeSet.put(write);
	}

	public void beforeReadAccess(Object obj, long field) {
		ReadFieldAccess next = readSet.getNext();
		next.init(obj, field);
		// Check the read is still valid
		lastReadLock = LockTable.checkAndGetLock(next.hashCode(), localClock.get());
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
