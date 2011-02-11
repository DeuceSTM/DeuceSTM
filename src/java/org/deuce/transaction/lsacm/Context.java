package org.deuce.transaction.lsacm;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.ConcurrentHashMap;

import org.deuce.transaction.TransactionException;
import org.deuce.transaction.lsacm.field.Field;
import org.deuce.transaction.lsacm.field.Field.Type;
import org.deuce.transaction.lsacm.field.WriteFieldAccess;
import org.deuce.transaction.lsacm.ReadSet;
import org.deuce.transaction.lsacm.WriteSet;
import org.deuce.transaction.lsacm.ContentionManager;
import org.deuce.transaction.lsacm.ContentionManager.ConflictType;
import org.deuce.transaction.util.BooleanArrayList;
import org.deuce.transform.Exclude;

/**
 * LSA implementation
 * 
 * @author Pascal Felber
 */
@Exclude
final public class Context implements org.deuce.transaction.Context {

	final private static int TX_IDLE = 0;
	final private static int TX_ACTIVE = 1;
	final private static int TX_COMMITTING = 2;
	final private static int TX_ABORTING = 3;
	final private static int TX_COMMITTED = 4;
	final private static int TX_ABORTED = 5;

	final private static int STATUS_BITS = 3;
	final private static int STATUS_MASK = (1 << STATUS_BITS) - 1;

	final private static TransactionException WRITE_FAILURE_EXCEPTION =
		new TransactionException("Fail on write (read previous version).");

	final private static TransactionException EXTEND_FAILURE_EXCEPTION =
		new TransactionException("Fail on extend.");

	final private static TransactionException READ_ONLY_FAILURE_EXCEPTION =
		new TransactionException("Fail on write (read-only hint was set).");

	final private static TransactionException KILLED_EXCEPTION =
		new TransactionException("Transaction has been killed.");

	final private static AtomicLong clock = new AtomicLong(0);
	final private static AtomicInteger threadID = new AtomicInteger(0);

	final private static Map<Integer, Context> threads = new ConcurrentHashMap<Integer, Context>();

	final private static boolean RO_HINT = Boolean.getBoolean("org.deuce.transaction.lsacm.rohint");

	final private static int VR_THRESHOLD = Integer.getInteger("org.deuce.transaction.lsacm.vr", 0);

	final private static ContentionManager cm;

	//Global lock used to allow only one irrevocable transaction solely. 
	final private static ReentrantReadWriteLock irrevocableAccessLock = new ReentrantReadWriteLock();
	private boolean irrevocableState = false;

	final private ReadSet readSet = new ReadSet(1024);
	final private WriteSet writeSet = new WriteSet(32);

	// Keep per-thread read-only hints (uses more memory but faster)
	final private BooleanArrayList readWriteMarkers = new BooleanArrayList();
	private boolean readWriteHint = true;
	private int atomicBlockId;

	private int readHash;
	private long readLock;
	private Object readValue;

	// Can be accessed (read) by other transaction during contention management
	private AtomicLong startTime;
	private long endTime;
	private int id;

	private int attempts;
	private boolean vr;

	private AtomicInteger status;

	static {
		// Set contention manager
		String s = System.getProperty("org.deuce.transaction.lsacm.cm");
		Object o = null;
		if (s != null) {
			try {
				o = Class.forName(s).newInstance();
			} catch (Exception e) {
				System.err.println("Cannot create contention manager: " + s);
			}
		}
		cm = (ContentionManager)o;
	}

	public Context() {
		// Unique identifier among active threads
		id = threadID.incrementAndGet();
		threads.put(id, this);
		attempts = 0;
		vr = false;
		startTime = new AtomicLong(0L);
		status = new AtomicInteger(TX_IDLE);
	}

	protected void finalize() throws Throwable {
		threads.remove(id);
		super.finalize();
	}

	@Override
	public void init(int blockId, String metainf) {
		readSet.clear();
		writeSet.clear();
		
		//Lock according to the transaction irrevocable state
		if(irrevocableState)
			irrevocableAccessLock.writeLock().lock();
		else
			irrevocableAccessLock.readLock().lock();
		
		endTime = clock.get();
		startTime.set(endTime);
		status.set(((status.get() + (1 << STATUS_BITS)) & ~STATUS_MASK) | TX_ACTIVE);
		if (RO_HINT) {
			atomicBlockId = blockId;
			readWriteHint = readWriteMarkers.get(atomicBlockId);
		}
		attempts++;
		vr = (VR_THRESHOLD > 0 && VR_THRESHOLD <= attempts);
	}

	@Override
	public boolean commit() {
		try{
			if (!writeSet.isEmpty()) {
				int v = status.get();
				int s = v & STATUS_MASK;
				if (s == TX_ACTIVE && status.compareAndSet(v, v + (TX_COMMITTING - TX_ACTIVE))) {
					long newClock = clock.incrementAndGet();
					if (newClock != startTime.get() + 1 && !readSet.validate(this, id)) {
						rollback0();
						return false;
					}
					// Write values and release locks
					writeSet.commit(newClock);
					status.set(v + (TX_COMMITTED - TX_ACTIVE));
				} else {
					// We have been killed: wait for our locks to have been released
					while (s != TX_ABORTED)
						s = status.get() & STATUS_MASK;
					return false;
				}
			} else {
				// No need to set status to COMMITTED (we cannot be killed with an empty write set)
			}
			attempts = 0;
			return true;
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

	@Override
	public void rollback() {
		rollback0();
		
		irrevocableAccessLock.readLock().unlock();
	}
	
	private void rollback0() {
		if (!writeSet.isEmpty()) {
			int v = status.get();
			int s = v & STATUS_MASK;
			if (s == TX_COMMITTING) {
				// Release locks
				writeSet.rollback();
				status.set(v + (TX_ABORTED - TX_COMMITTING));
			} else if (s == TX_ACTIVE && status.compareAndSet(v, v + (TX_ABORTING - TX_ACTIVE))) {
				// Release locks
				writeSet.rollback();
				status.set(v + (TX_ABORTED - TX_ACTIVE));
			} else {
				// We have been killed: wait for our locks to have been released
				while (s != TX_ABORTED)
					s = status.get() & STATUS_MASK;
			}
		} else {
			// No need to set status to ABORTED (at that point we do not hold locks anymore)
		}
		
		irrevocableAccessLock.readLock().unlock();
	}

	public boolean conflict(int other, ConflictType type, int hash, long lock) {
		if (cm != null) {
			Context tx = threads.get(other);
			if (cm.arbitrate(this, tx, type) == ContentionManager.KILL_OTHER) {
				// We win
				synchronized (tx.writeSet) {
					// Mutual exclusion on write set to drop locks
					if (lock == LockTable.readLock(hash)) {
						// The other transaction still owns the lock
						kill(tx);
					}
				}
				return true;
			}
		}
		return false;
	}

	public static void kill(Context tx) {
		// Kill other transaction
		int v = tx.status.get();
		int s = v & STATUS_MASK;
		if (s == TX_ACTIVE && tx.status.compareAndSet(v, v + (TX_ABORTING - TX_ACTIVE))) {
			// Release locks
			tx.writeSet.rollback();
			tx.status.set(v + (TX_ABORTED - TX_ACTIVE));
		}
	}

	public long getStartTime() {
		return startTime.get();
	}

	public int getId() {
		return id;
	}

	public boolean isActive() {
		return (status.get() & STATUS_MASK) == TX_ACTIVE;
	}

	private boolean extend() {
		long now = clock.get();
		if (readSet.validate(this, id)) {
			endTime = now;
			return true;
		}
		return false;
	}

	@Override
	public void beforeReadAccess(Object obj, long field) {
		if (vr) {
			readHash = LockTable.hash(obj, field);
			// Lock entry in read mode (might throw an exception)
			readLock = LockTable.lock(this, readHash, id, false);
			if (readLock >= 0) {
				synchronized (writeSet) {
					// Mutual exclusion on write set to allow other transaction to drop locks
					if ((status.get() & STATUS_MASK) != TX_ACTIVE) {
						// We have been killed: drop lock we just acquired (not in write set)
						LockTable.setAndReleaseLock(readHash, readLock);
						// Abort
						throw KILLED_EXCEPTION;
					}
					// Add to write set (for being able to drop lock later)
					writeSet.addRead(readHash, obj, field, readLock);
				}
			}
		} else {
			readHash = LockTable.hash(obj, field);
			// Check if the field is locked (may throw an exception)
			readLock = LockTable.checkLock(this, readHash, id);
		}
	}

	private boolean onReadAccess(Object obj, long field, Type type) {
		if (vr) {
			// Visible read
			if (readLock == LockTable.LOCKED_WRITE) {
				// We already own that lock in write mode
				WriteFieldAccess w = writeSet.get(readHash, obj, field);
				if (w == null)
					return false;
				readValue = w.getValue();
				return true;
			} else {
				// We already own that lock in read mode
				return false;
			}
		} else {
			// Invisible read
			if ((status.get() & STATUS_MASK) != TX_ACTIVE) {
				// We have been killed: abort
				throw KILLED_EXCEPTION;
			}
			if (readLock == LockTable.LOCKED_WRITE) {
				// We already own that lock in write mode
				WriteFieldAccess w = writeSet.get(readHash, obj, field);
				if (w == null)
					return false;
				readValue = w.getValue();
				return true;
			} else if (readLock == LockTable.LOCKED_READ) {
				// We already own that lock in read mode
				return false;
			}
			boolean b = false;
			while (true) {
				while (readLock <= endTime) {
					// Re-read timestamp (check for race)
					long lock = LockTable.checkLock(this, readHash, id);
					if (lock != readLock) {
						readLock = lock;
						readValue = Field.getValue(obj, field, type);
						b = true;
						continue;
					}
					// We have read a valid value (in snapshot)
					if (readWriteHint) {
						// Save to read set
						readSet.add(obj, field, readHash, lock);
					}
					return b;
				}

				// Try to extend snapshot
				if (!(readWriteHint && extend())) {
					throw EXTEND_FAILURE_EXCEPTION;
				}
			}
		}
	}

	private void onWriteAccess(Object obj, long field, Object value, Type type) {
		if (!readWriteHint) {
			// Change hint to read-write
			readWriteMarkers.insert(atomicBlockId, true);
			throw READ_ONLY_FAILURE_EXCEPTION;
		}

		int hash = LockTable.hash(obj, field);
		// Lock entry in write mode (might throw an exception)
		long timestamp = LockTable.lock(this, hash, id, true);

		synchronized (writeSet) {
			// Mutual exclusion on write set to allow other transaction to drop locks
			if ((status.get() & STATUS_MASK) != TX_ACTIVE) {
				// We have been killed
				if (timestamp >= 0) {
					// Drop lock we just acquired (not in write set)
					LockTable.setAndReleaseLock(hash, timestamp);
				}
				// Abort
				throw KILLED_EXCEPTION;
			}
			if (timestamp < 0) {
				// We already own that lock
				writeSet.appendWrite(hash, obj, field, value, type);
			} else {
				// Add to write set
				if (timestamp > endTime) {
					// Handle write-after-read
					if (readSet.contains(obj, field)) {
						// Abort
						LockTable.setAndReleaseLock(hash, timestamp);
						throw WRITE_FAILURE_EXCEPTION;
					}
					// We delay validation until later (although we could already validate once here)
				}
				// Add to write set
				writeSet.addWrite(hash, obj, field, value, type, timestamp);
			}
		}
	}

	@Override
	public Object onReadAccess(Object obj, Object value, long field) {
		return (onReadAccess(obj, field, Type.OBJECT) ? readValue : value);
	}

	@Override
	public boolean onReadAccess(Object obj, boolean value, long field) {
		return (onReadAccess(obj, field, Type.BOOLEAN) ? (Boolean) readValue : value);
	}

	@Override
	public byte onReadAccess(Object obj, byte value, long field) {
		return (onReadAccess(obj, field, Type.BYTE) ? ((Number) readValue).byteValue() : value);
	}

	@Override
	public char onReadAccess(Object obj, char value, long field) {
		return (onReadAccess(obj, field, Type.CHAR) ? (Character) readValue : value);
	}

	@Override
	public short onReadAccess(Object obj, short value, long field) {
		return (onReadAccess(obj, field, Type.SHORT) ? ((Number) readValue).shortValue() : value);
	}

	@Override
	public int onReadAccess(Object obj, int value, long field) {
		return (onReadAccess(obj, field, Type.INT) ? ((Number) readValue).intValue() : value);
	}

	@Override
	public long onReadAccess(Object obj, long value, long field) {
		return (onReadAccess(obj, field, Type.LONG) ? ((Number) readValue).longValue() : value);
	}

	@Override
	public float onReadAccess(Object obj, float value, long field) {
		return (onReadAccess(obj, field, Type.FLOAT) ? ((Number) readValue).floatValue() : value);
	}

	@Override
	public double onReadAccess(Object obj, double value, long field) {
		return (onReadAccess(obj, field, Type.DOUBLE) ? ((Number) readValue).doubleValue() : value);
	}

	@Override
	public void onWriteAccess(Object obj, Object value, long field) {
		onWriteAccess(obj, field, value, Type.OBJECT);
	}

	@Override
	public void onWriteAccess(Object obj, boolean value, long field) {
		onWriteAccess(obj, field, (Object) value, Type.BOOLEAN);
	}

	@Override
	public void onWriteAccess(Object obj, byte value, long field) {
		onWriteAccess(obj, field, (Object) value, Type.BYTE);
	}

	@Override
	public void onWriteAccess(Object obj, char value, long field) {
		onWriteAccess(obj, field, (Object) value, Type.CHAR);
	}

	@Override
	public void onWriteAccess(Object obj, short value, long field) {
		onWriteAccess(obj, field, (Object) value, Type.SHORT);
	}

	@Override
	public void onWriteAccess(Object obj, int value, long field) {
		onWriteAccess(obj, field, (Object) value, Type.INT);
	}

	@Override
	public void onWriteAccess(Object obj, long value, long field) {
		onWriteAccess(obj, field, (Object) value, Type.LONG);
	}

	@Override
	public void onWriteAccess(Object obj, float value, long field) {
		onWriteAccess(obj, field, (Object) value, Type.FLOAT);
	}

	@Override
	public void onWriteAccess(Object obj, double value, long field) {
		onWriteAccess(obj, field, (Object) value, Type.DOUBLE);
	}

	@Override
	public void onIrrevocableAccess() {
		if(irrevocableState) // already in irrevocable state so no need to restart transaction.
			return;

		irrevocableState = true;
		throw TransactionException.STATIC_TRANSACTION;
	}
}
