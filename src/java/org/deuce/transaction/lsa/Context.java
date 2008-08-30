package org.deuce.transaction.lsa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.deuce.transaction.AbstractContext;
import org.deuce.transaction.TransactionException;
import org.deuce.transaction.lsa.field.Field;
import org.deuce.transaction.lsa.field.ReadFieldAccess;
import org.deuce.transaction.lsa.field.WriteFieldAccess;
import org.deuce.transaction.lsa.field.WriteFieldAccess.Type;
import org.deuce.transform.Exclude;

/**
 * LSA implementation
 * 
 * @author Pascal Felber
 * @since 0.1
 */
@Exclude
final public class Context extends AbstractContext {
	final private static AtomicInteger clock = new AtomicInteger(0);
	final private static AtomicInteger threadID = new AtomicInteger(0);
	private static final Logger logger = Logger.getLogger("org.deuce.transaction.lsa");

	final private ArrayList<ReadFieldAccess> readSet = new ArrayList<ReadFieldAccess>(1024);
	final private HashMap<Integer, WriteFieldAccess> writeSet = new HashMap<Integer, WriteFieldAccess>(32);

	private int startTime;
	private int endTime;
	private int id;

	public Context() {
		// Unique identifier among active threads (could use Thread.currentThread().getId() but it is a long)
		id = threadID.incrementAndGet();
	}

	@Override
	protected void init() {
		logger.fine("Init transaction.");
		this.readSet.clear();
		this.writeSet.clear();
		this.startTime = this.endTime = clock.get();
	}

	@Override
	public boolean commit() {
		logger.fine("Start to commit.");

		if (!writeSet.isEmpty()) {
			int newClock = clock.incrementAndGet();
			if (newClock != startTime + 1 && !validate(newClock)) {
				rollback();
				logger.fine("Fail on commit.");
				return false;
			}
			// Write values and release locks
			for (WriteFieldAccess w : writeSet.values()) {
				int hash = w.getHash();
				int lock = w.getLock();
				assert lock >= 0;
				do {
					w.writeField();
					w = w.getNext();
				} while (w != null);
				LockTable.setAndReleaseLock(hash, lock);
			}
		}
		logger.fine("Commit successed.");
		return true;
	}

	@Override
	public void rollback() {
		logger.fine("Start to rollback.");
		// Release locks
		for (WriteFieldAccess w : writeSet.values()) {
			assert w.getLock() >= 0;
			LockTable.setAndReleaseLock(w.getHash(), w.getLock());
		}
		logger.fine("Rollback successed.");
	}

	private boolean validate(int timestamp) {
		try {
			for (ReadFieldAccess r : readSet) {
				// Throws an exception if validation fails
				int lock = LockTable.checkLock(r.getHash(), timestamp);
				if (lock >= 0 && lock != r.getLock()) {
					// Other version: cannot validate
					return false;
				}
			}
		} catch (TransactionException e) {
			return false;
		}
		return true;
	}

	private boolean extend() {
		int now = clock.get();
		if (validate(now)) {
			endTime = now;
			return true;
		}
		return false;
	}

	public Object addReadAccess(Object obj, long field, Object value, Type type) {

		logger.finest("Read access.");

		int hash = LockTable.hash(obj, field);

		while (true) {
			// Check if the field is locked (may throw an exception)
			int timestamp = LockTable.checkLock(hash, id);

			if (timestamp < 0) {
				// We already own that lock
				WriteFieldAccess w = writeSet.get(hash);
				assert w != null;
				Field f = new Field(obj, field);
				while (true) {
					// Check if we have already read that field
					if (f.equals(w)) {
						return w.getValue();
					}
					w = w.getNext();
					if (w == null) {
						// We did not read this field (but no need to add it to read set)
						return value;
					}
				}
			}

			if (timestamp <= endTime) {
				// We have read a valid value (in snapshot)
				ReadFieldAccess read = new ReadFieldAccess(obj, field, hash, timestamp);
				// Save to read set
				readSet.add(read);
				return value;
			}

			// Try to extend snapshot
			if (!extend()) {
				throw new TransactionException("Fail on extend.");
			}
			// We need to re-read the value as a new version might have been written
			value = Field.getValue(obj, field, type);
		}
	}

	private void addWriteAccess(Object obj, long field, Object value, Type type) {

		logger.finer("Write access.");

		int hash = LockTable.hash(obj, field);

		// Lock entry (might throw an exception upon abort)
		int timestamp = LockTable.lock(hash, id);

		if (timestamp < 0) {
			// We already own that lock
			WriteFieldAccess w = writeSet.get(hash);
			Field f = new Field(obj, field);
			while (true) {
				// Check if we have already written that field
				if (f.equals(w)) {
					// Update written value
					w.setValue(value);
					return;
				}
				if (w.getNext() == null) {
					// We did not write this field (we must add it to read set)
					w.setNext(new WriteFieldAccess(obj, field, type, value, hash, timestamp));
					return;
				}
				w = w.getNext();
			}
		}

		if (timestamp > endTime) {
			// Handle write-after-read
			Field f = new Field(obj, field);
			for (ReadFieldAccess r : readSet) {
				if (f.equals(r)) {
					// Abort
					throw new TransactionException("Fail on write (read previous version).");
				}
				// We delay validation until later (although we could already validate once here)
			}
		}

		// Create write set entry
		WriteFieldAccess write = new WriteFieldAccess(obj, field, type, value, hash, timestamp);
		// Add to write set
		writeSet.put(hash, write);
	}

	@Override
	public Object addReadAccess(Object obj, Object value, long field) {
		return addReadAccess(obj, field, value, Type.OBJECT);
	}

	@Override
	public boolean addReadAccess(Object obj, boolean value, long field) {
		return (Boolean) addReadAccess(obj, field, (Object) value, Type.BOOLEAN);
	}

	@Override
	public byte addReadAccess(Object obj, byte value, long field) {
		return ((Number) addReadAccess(obj, field, (Object) value, Type.BYTE)).byteValue();
	}

	@Override
	public char addReadAccess(Object obj, char value, long field) {
		return (Character) addReadAccess(obj, field, (Object) value, Type.CHAR);
	}

	@Override
	public short addReadAccess(Object obj, short value, long field) {
		return ((Number) addReadAccess(obj, field, (Object) value, Type.SHORT)).shortValue();
	}

	@Override
	public int addReadAccess(Object obj, int value, long field) {
		return ((Number) addReadAccess(obj, field, (Object) value, Type.INT)).intValue();
	}

	@Override
	public long addReadAccess(Object obj, long value, long field) {
		return ((Number) addReadAccess(obj, field, (Object) value, Type.LONG)).longValue();
	}

	@Override
	public float addReadAccess(Object obj, float value, long field) {
		return ((Number) addReadAccess(obj, field, (Object) value, Type.FLOAT)).floatValue();
	}

	@Override
	public double addReadAccess(Object obj, double value, long field) {
		return ((Number) addReadAccess(obj, field, (Object) value, Type.DOUBLE)).doubleValue();
	}

	@Override
	public void addWriteAccess(Object obj, Object value, long field) {
		addWriteAccess(obj, field, value, Type.OBJECT);
	}

	@Override
	public void addWriteAccess(Object obj, boolean value, long field) {
		addWriteAccess(obj, field, (Object) value, Type.BOOLEAN);
	}

	@Override
	public void addWriteAccess(Object obj, byte value, long field) {
		addWriteAccess(obj, field, (Object) value, Type.BYTE);
	}

	@Override
	public void addWriteAccess(Object obj, char value, long field) {
		addWriteAccess(obj, field, (Object) value, Type.CHAR);
	}

	@Override
	public void addWriteAccess(Object obj, short value, long field) {
		addWriteAccess(obj, field, (Object) value, Type.SHORT);
	}

	@Override
	public void addWriteAccess(Object obj, int value, long field) {
		addWriteAccess(obj, field, (Object) value, Type.INT);
	}

	@Override
	public void addWriteAccess(Object obj, long value, long field) {
		addWriteAccess(obj, field, (Object) value, Type.LONG);
	}

	@Override
	public void addWriteAccess(Object obj, float value, long field) {
		addWriteAccess(obj, field, (Object) value, Type.FLOAT);
	}

	@Override
	public void addWriteAccess(Object obj, double value, long field) {
		addWriteAccess(obj, field, (Object) value, Type.DOUBLE);
	}
}
