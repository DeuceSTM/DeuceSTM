package org.deuce.transaction.lsa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.deuce.transaction.TransactionException;
import org.deuce.transaction.lsa.field.Field;
import org.deuce.transaction.lsa.field.Field.Type;
import org.deuce.transaction.lsa.field.ReadFieldAccess;
import org.deuce.transaction.lsa.field.WriteFieldAccess;
import org.deuce.transform.Exclude;

/**
 * LSA implementation
 * 
 * @author Pascal Felber
 * @since 0.1
 */
@Exclude
final public class Context implements org.deuce.transaction.Context {
	
	// Failure transaction 
	final private static TransactionException WRITE_FAILURE_EXCEPTION = 
		new TransactionException("Fail on write (read previous version).");
	
	final private static TransactionException EXTEND_FAILURE_EXCEPTION =  
		new TransactionException("Fail on extend.");
	
	final private static AtomicInteger clock = new AtomicInteger(0);
	final private static AtomicInteger threadID = new AtomicInteger(0);
	private static final Logger logger = Logger.getLogger("org.deuce.transaction.lsa");

	final private ArrayList<ReadFieldAccess> readSet = new ArrayList<ReadFieldAccess>(1024);
	final private HashMap<Integer, WriteFieldAccess> writeSet = new HashMap<Integer, WriteFieldAccess>(32);

	private int startTime;
	private int endTime;
	private int id;

	public Context() {
		// Unique identifier among active threads
		id = threadID.incrementAndGet();
	}

	public void init(String method) {
		logger.fine("Init transaction.");
		this.readSet.clear();
		this.writeSet.clear();
		this.startTime = this.endTime = clock.get();
	}

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
				LockTable.setAndReleaseLock(hash, newClock);
			}
		}
		logger.fine("Commit successed.");
		return true;
	}

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

	public Object addReadAccess(Object obj, long field, Type type) {

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
						return Field.getValue(obj, field, type);
					}
				}
			}

			while (timestamp <= endTime) {
				Object value = Field.getValue(obj, field, type);
				// Re-read timestamp (check for race)
				int timestamp2 = LockTable.checkLock(hash, id);
				if (timestamp != timestamp2) {
					timestamp = timestamp2;
					continue;
				}
				// We have read a valid value (in snapshot)
				ReadFieldAccess read = new ReadFieldAccess(obj, field, hash, timestamp);
				// Save to read set
				readSet.add(read);
				return value;
			}

			// Try to extend snapshot
			if (!extend()) {
				throw EXTEND_FAILURE_EXCEPTION;
			}
		}
	}

	private void addWriteAccess(Object obj, long field, Object value, Type type) {

		logger.finer("Write access.");

		int hash = LockTable.hash(obj, field);

		// Lock entry (might throw an exception)
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
				WriteFieldAccess next = w.getNext();
				if (next == null) {
					// We did not write this field (we must add it to read set)
					w.setNext(new WriteFieldAccess(obj, field, type, value, hash, timestamp));
					return;
				}
				w = next;
			}
		}

		if (timestamp > endTime) {
			// Handle write-after-read
			Field f = new Field(obj, field);
			for (ReadFieldAccess r : readSet) {
				if (f.equals(r)) {
					// Abort
					LockTable.setAndReleaseLock(hash, timestamp);
					throw WRITE_FAILURE_EXCEPTION;
				}
				// We delay validation until later (although we could already validate once here)
			}
		}

		// Create write set entry
		WriteFieldAccess write = new WriteFieldAccess(obj, field, type, value, hash, timestamp);
		// Add to write set
		writeSet.put(hash, write);
	}

	public Object addReadAccess(Object obj, Object value, long field) {
		return addReadAccess(obj, field, Type.OBJECT);
	}

	public boolean addReadAccess(Object obj, boolean value, long field) {
		return (Boolean) addReadAccess(obj, field, Type.BOOLEAN);
	}

	public byte addReadAccess(Object obj, byte value, long field) {
		return ((Number) addReadAccess(obj, field, Type.BYTE)).byteValue();
	}

	public char addReadAccess(Object obj, char value, long field) {
		return (Character) addReadAccess(obj, field, Type.CHAR);
	}

	public short addReadAccess(Object obj, short value, long field) {
		return ((Number) addReadAccess(obj, field, Type.SHORT)).shortValue();
	}

	public int addReadAccess(Object obj, int value, long field) {
		return ((Number) addReadAccess(obj, field, Type.INT)).intValue();
	}

	public long addReadAccess(Object obj, long value, long field) {
		return ((Number) addReadAccess(obj, field, Type.LONG)).longValue();
	}

	public float addReadAccess(Object obj, float value, long field) {
		return ((Number) addReadAccess(obj, field, Type.FLOAT)).floatValue();
	}

	public double addReadAccess(Object obj, double value, long field) {
		return ((Number) addReadAccess(obj, field, Type.DOUBLE)).doubleValue();
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
	
	public void beforeReadAccess(Object obj, long field) {
	}
}
