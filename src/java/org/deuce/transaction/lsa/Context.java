package org.deuce.transaction.lsa;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.deuce.transaction.TransactionException;
import org.deuce.transaction.lsa.field.Field;
import org.deuce.transaction.lsa.field.Field.Type;
import org.deuce.transaction.lsa.field.WriteFieldAccess;
import org.deuce.transaction.lsa.ReadSet;
import org.deuce.transaction.lsa.WriteSet;
import org.deuce.transaction.util.BooleanArrayList;
import org.deuce.transform.Exclude;

/**
 * LSA implementation
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

	final private static AtomicInteger clock = new AtomicInteger(0);
	final private static AtomicInteger threadID = new AtomicInteger(0);

	final private static boolean RO_HINT = Boolean.getBoolean("org.deuce.transaction.lsa.rohint");

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
	private int readLock;
	private Object readValue;

	private int startTime;
	private int endTime;
	private int id;

	public Context() {
		// Unique identifier among active threads
		id = threadID.incrementAndGet();
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
		
		startTime = endTime = clock.get();
		if (RO_HINT) {
			atomicBlockId = blockId;
			readWriteHint = readWriteMarkers.get(atomicBlockId);
		}
	}

	@Override
	public boolean commit() {
		try{
			if (!writeSet.isEmpty()) {
				int newClock = clock.incrementAndGet();
				if (newClock != startTime + 1 && !readSet.validate(id)) {
					writeSet.rollback(); // Release locks
					return false;
				}
				// Write values and release locks
				writeSet.commit(newClock);
			}
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
		// Release locks
		writeSet.rollback();
		irrevocableAccessLock.readLock().unlock();
	}

	private boolean extend() {
		int now = clock.get();
		if (readSet.validate(id)) {
			endTime = now;
			return true;
		}
		return false;
	}

	@Override
	public void beforeReadAccess(Object obj, long field) {
		readHash = LockTable.hash(obj, field);
		// Check if the field is locked (may throw an exception)
		readLock = LockTable.checkLock(readHash, id);
	}

	private boolean onReadAccess(Object obj, long field, Type type) {
		if (readLock < 0) {
			// We already own that lock
			WriteFieldAccess w = writeSet.get(readHash, obj, field);
			if (w == null)
				return false;
			readValue = w.getValue();
			return true;
		}
		boolean b = false;
		while (true) {
			while (readLock <= endTime) {
				// Re-read timestamp (check for race)
				int lock = LockTable.checkLock(readHash, id);
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

	private void onWriteAccess(Object obj, long field, Object value, Type type) {
		if (!readWriteHint) {
			// Change hint to read-write
			readWriteMarkers.insert(atomicBlockId, true);
			throw READ_ONLY_FAILURE_EXCEPTION;
		}

		int hash = LockTable.hash(obj, field);

		// Lock entry (might throw an exception)
		int timestamp = LockTable.lock(hash, id);

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
