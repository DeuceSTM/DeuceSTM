package org.deuce.transaction.lsa64;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.deuce.transaction.TransactionException;
import org.deuce.transaction.lsa64.field.Field;
import org.deuce.transaction.lsa64.field.Field.Type;
import org.deuce.transaction.lsa64.ReadSet;
import org.deuce.transaction.lsa64.WriteSet;
import org.deuce.transaction.util.BooleanArrayList;
import org.deuce.transform.Exclude;

/**
 * LSA implementation (64-bit locks)
 * 
 * @author Pascal Felber
 */
@Exclude
final public class Context implements org.deuce.transaction.Context {

	final private static TransactionException WRITE_FAILURE_EXCEPTION =
		new TransactionException("Fail on write (read previous version).");

	final private static TransactionException EXTEND_FAILURE_EXCEPTION =
		new TransactionException("Fail on extend.");

	final private static TransactionException READ_ONLY_FAILURE_EXCEPTION =
		new TransactionException("Fail on write (read-only hint was set).");

	final private static AtomicLong clock = new AtomicLong(0);
	final private static AtomicLong threadID = new AtomicLong(0);
	final private static Logger logger = Logger.getLogger("org.deuce.transaction.lsa");

	final private static boolean RO_HINT = Boolean.getBoolean("org.deuce.transaction.lsa.rohint");
	final private static boolean READ_LOCKED = Boolean.getBoolean("org.deuce.transaction.lsa.readlocked");

	final private ReadSet readSet = new ReadSet(1024);
	final private WriteSet writeSet = new WriteSet(32);

	// Keep per-thread read-only hints (uses more memory but faster)
	final private BooleanArrayList readWriteMarkers = new BooleanArrayList();
	private boolean readWriteHint = true;
	private int atomicBlockId;

	private int readHash;
	private long readLock;

	private long startTime;
	private long endTime;
	private long id;

	public Context() {
		// Unique identifier among active threads
		id = threadID.incrementAndGet();
	}

	public void init(int blockId) {
		logger.fine("Init transaction.");
		readSet.clear();
		writeSet.clear();
		startTime = endTime = clock.get();
		if (RO_HINT) {
			atomicBlockId = blockId;
			readWriteHint = readWriteMarkers.get(atomicBlockId);
		}
	}

	public boolean commit() {
		logger.fine("Start to commit.");
		if (!writeSet.isEmpty()) {
			long newClock = clock.incrementAndGet();
			if (newClock != startTime + 1 && !readSet.validate(id)) {
				rollback();
				logger.fine("Fail on commit.");
				return false;
			}
			// Write values and release locks
			writeSet.commit(newClock);
		}
		logger.fine("Commit successed.");
		return true;
	}

	public void rollback() {
		logger.fine("Start to rollback.");
		// Release locks
		writeSet.rollback();
		logger.fine("Rollback successed.");
	}

	private boolean extend() {
		long now = clock.get();
		if (readSet.validate(id)) {
			endTime = now;
			return true;
		}
		return false;
	}

	public void beforeReadAccess(Object obj, long field) {
		logger.finest("Before read access.");

		readHash = LockTable.hash(obj, field);
		// Check if the field is locked (may throw an exception)
		readLock = LockTable.checkLock(readHash, id);
	}

	public Object addReadAccess(Object obj, long field, Type type) {
		logger.finest("Read access.");

		while (true) {
			if (readLock < 0) {
				// We already own that lock
				return writeSet.get(readHash, obj, field);
			}

			Object value = null;
			while (readLock <= endTime) {
				// Re-read timestamp (check for race)
				long lock = LockTable.checkLock(readHash, id);
				if (lock != readLock) {
					readLock = lock;
					value = Field.getValue(obj, field, type);
					continue;
				}
				// We have read a valid value (in snapshot)
				if (readWriteHint) {
					// Save to read set
					readSet.add(obj, field, readHash, lock);
				}
				return value;
			}

			// Try to extend snapshot
			if (!(readWriteHint && extend())) {
				throw EXTEND_FAILURE_EXCEPTION;
			}
		}
	}

	private void addWriteAccess(Object obj, long field, Object value, Type type) {
		logger.finer("Write access.");

		if (!readWriteHint) {
			// Change hint to read-write
			readWriteMarkers.insert(atomicBlockId, true);
			throw READ_ONLY_FAILURE_EXCEPTION;
		}

		int hash = LockTable.hash(obj, field);

		// Lock entry (might throw an exception)
		long timestamp = LockTable.lock(hash, id);

		if (timestamp < 0) {
			// We already own that lock
			writeSet.append(hash, obj, field, value, type);
			return;
		}

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
		writeSet.add(hash, obj, field, value, type, timestamp);
	}

	public Object addReadAccess(Object obj, Object value, long field) {
		Object v = addReadAccess(obj, field, Type.OBJECT);
		return (v == null ? value : v);
	}

	public boolean addReadAccess(Object obj, boolean value, long field) {
		Object v = addReadAccess(obj, field, Type.BOOLEAN);
		return (v == null ? value : (Boolean) v);
	}

	public byte addReadAccess(Object obj, byte value, long field) {
		Object v = addReadAccess(obj, field, Type.BYTE);
		return (v == null ? value : ((Number) v).byteValue());
	}

	public char addReadAccess(Object obj, char value, long field) {
		Object v = addReadAccess(obj, field, Type.CHAR);
		return (v == null ? value : (Character) v);
	}

	public short addReadAccess(Object obj, short value, long field) {
		Object v = addReadAccess(obj, field, Type.SHORT);
		return (v == null ? value : ((Number) v).shortValue());
	}

	public int addReadAccess(Object obj, int value, long field) {
		Object v = addReadAccess(obj, field, Type.INT);
		return (v == null ? value : ((Number) v).intValue());
	}

	public long addReadAccess(Object obj, long value, long field) {
		Object v = addReadAccess(obj, field, Type.LONG);
		return (v == null ? value : ((Number) v).longValue());
	}

	public float addReadAccess(Object obj, float value, long field) {
		Object v = addReadAccess(obj, field, Type.FLOAT);
		return (v == null ? value : ((Number) v).floatValue());
	}

	public double addReadAccess(Object obj, double value, long field) {
		Object v = addReadAccess(obj, field, Type.DOUBLE);
		return (v == null ? value : ((Number) v).doubleValue());
	}

	public void addWriteAccess(Object obj, Object value, long field) {
		addWriteAccess(obj, field, value, Type.OBJECT);
	}

	public void addWriteAccess(Object obj, boolean value, long field) {
		addWriteAccess(obj, field, (Object) value, Type.BOOLEAN);
	}

	public void addWriteAccess(Object obj, byte value, long field) {
		addWriteAccess(obj, field, (Object) value, Type.BYTE);
	}

	public void addWriteAccess(Object obj, char value, long field) {
		addWriteAccess(obj, field, (Object) value, Type.CHAR);
	}

	public void addWriteAccess(Object obj, short value, long field) {
		addWriteAccess(obj, field, (Object) value, Type.SHORT);
	}

	public void addWriteAccess(Object obj, int value, long field) {
		addWriteAccess(obj, field, (Object) value, Type.INT);
	}

	public void addWriteAccess(Object obj, long value, long field) {
		addWriteAccess(obj, field, (Object) value, Type.LONG);
	}

	public void addWriteAccess(Object obj, float value, long field) {
		addWriteAccess(obj, field, (Object) value, Type.FLOAT);
	}

	public void addWriteAccess(Object obj, double value, long field) {
		addWriteAccess(obj, field, (Object) value, Type.DOUBLE);
	}
}
