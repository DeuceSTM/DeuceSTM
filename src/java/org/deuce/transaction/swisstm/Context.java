package org.deuce.transaction.swisstm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.deuce.transaction.TransactionException;
import org.deuce.transaction.lsa.field.Field.Type;
import org.deuce.transaction.swisstm.field.AddressLocks;
import org.deuce.transform.Exclude;

/**
 * SwissTM implementation
 *
 * TODO:
 *  - irrevocableState
 *  - RO_HINT
 *  - Contention Manager
 *  - rollback
 *  - commit
 *  - validate
 *  - TODOs in read and write
 *
 * @author Daniel Pinto
 */
@Exclude
final public class Context implements org.deuce.transaction.Context {

	// Exceptions
	private static final TransactionException WRITE_FAILURE_EXCEPTION =
			new TransactionException("Fail on write.");

	private static final TransactionException READ_FAILURE_EXCEPTION =
			new TransactionException("Fail on read.");

	// Global variables
	private static final AtomicInteger threadID = new AtomicInteger(1);
	private static final AtomicInteger commitTS = new AtomicInteger(0);
	private static final LockTable lockTable = new LockTable();

	// Transaction local variables
	private final int id;
	private int validTS;

	private Object readValue;

	// TODO: Isolate these into classes to hide Address class
	private final Map<Address, ReadLogEntry> readLog;
	private final Map<Address, WriteLogEntry> writeLog;
	private final Set<Address> readLockedAddresses;

	public Context() {
		this.id = threadID.incrementAndGet();
		this.readLog = new HashMap<Address, ReadLogEntry>();
		this.writeLog = new HashMap<Address, WriteLogEntry>();
		this.readLockedAddresses = new HashSet<Address>();
	}

	@Override
	public void init(int atomicBlockId, String metainf) {
		this.validTS = commitTS.get();
	}

	@Override
	public boolean commit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void rollback() {
		// TODO Auto-generated method stub

	}

	private boolean extend() {
		int ts = commitTS.get();
		if (validate()) {
			this.validTS = ts;
			return true;
		}

		return false;
	}

	private boolean validate() {
		// TODO
		return true;
	}



	@Override
	public void beforeReadAccess(Object obj, long field) {
		// Useless for SwissTM

	}

	// Returns true if the value should be read from memory or false
	// if it is in readValue
	private boolean onReadAccess(Object obj, long field, Type type) {
		AddressLocks locks = lockTable.getLocks(obj, field);
		boolean shouldReturnLogValue = true;

		if (locks.getWLockThreadID() == this.id) { // Locked by me?
			// TODO: Put the value last written in this.readValue
			return shouldReturnLogValue;
		}

		int version = locks.getRLockVersion();
		int version2;
		while (true) {
			if (version == AddressLocks.READ_LOCKED) {
				version = locks.getRLockVersion();
				continue;
			}

			shouldReturnLogValue = false;

			version2 = locks.getRLockVersion();
			if (version == version2) {
				break;
			}

			version2 = version;
		}

		// TODO: Add lock to list of locks locked by this tx

		if (version > this.validTS && !extend()) {
			// Is calling rollback needed???
			throw READ_FAILURE_EXCEPTION;
		}

		return shouldReturnLogValue;
	}

	private void onWriteAccess(Object obj, long field, Object value, Type type) {
		AddressLocks locks = lockTable.getLocks(obj, field);

		if (locks.getWLockThreadID() == this.id) { // Locked by me?
			// TODO: update-log-entry(w-lock, addr, value)
			return;
		}

		while (true) {
			if (locks.getWLockThreadID() != AddressLocks.WRITE_UNLOCKED) {
				// TODO
				// if cm-should-abort(tx, w-lock) then rollback()
				// else continue

				throw WRITE_FAILURE_EXCEPTION;
			}
			// TODO
			// add-to-write-log(tx, w-lock, addr, value)

			if (locks.casWLock(AddressLocks.WRITE_UNLOCKED, this.id)) {
				break;
			}
		}

		if (locks.getRLockVersion() > this.validTS && !extend()) {
			// Is calling rollback needed???
			throw WRITE_FAILURE_EXCEPTION;
		}

		// TODO: cm-on-write(tx)

	}

	@Override
	public Object onReadAccess(Object obj, Object value, long field) {
		return (onReadAccess(obj, field, Type.OBJECT) ? this.readValue : value);
	}

	@Override
	public boolean onReadAccess(Object obj, boolean value, long field) {
		return (onReadAccess(obj, field, Type.BOOLEAN) ? (Boolean) this.readValue : value);
	}

	@Override
	public byte onReadAccess(Object obj, byte value, long field) {
		return (onReadAccess(obj, field, Type.BYTE) ? ((Number) this.readValue).byteValue() : value);
	}

	@Override
	public char onReadAccess(Object obj, char value, long field) {
		return (onReadAccess(obj, field, Type.CHAR) ? (Character) this.readValue : value);
	}

	@Override
	public short onReadAccess(Object obj, short value, long field) {
		return (onReadAccess(obj, field, Type.SHORT) ? ((Number) this.readValue).shortValue() : value);
	}

	@Override
	public int onReadAccess(Object obj, int value, long field) {
		return (onReadAccess(obj, field, Type.INT) ? ((Number) this.readValue).intValue() : value);
	}

	@Override
	public long onReadAccess(Object obj, long value, long field) {
		return (onReadAccess(obj, field, Type.LONG) ? ((Number) this.readValue).longValue() : value);
	}

	@Override
	public float onReadAccess(Object obj, float value, long field) {
		return (onReadAccess(obj, field, Type.FLOAT) ? ((Number) this.readValue).floatValue() : value);
	}

	@Override
	public double onReadAccess(Object obj, double value, long field) {
		return (onReadAccess(obj, field, Type.DOUBLE) ? ((Number) this.readValue).doubleValue() : value);
	}

	@Override
	public void onWriteAccess(Object obj, Object value, long field) {
		onWriteAccess(obj, field, value, Type.OBJECT);
	}

	@Override
	public void onWriteAccess(Object obj, boolean value, long field) {
		onWriteAccess(obj, field, value, Type.BOOLEAN);
	}

	@Override
	public void onWriteAccess(Object obj, byte value, long field) {
		onWriteAccess(obj, field, value, Type.BYTE);
	}

	@Override
	public void onWriteAccess(Object obj, char value, long field) {
		onWriteAccess(obj, field, value, Type.CHAR);
	}

	@Override
	public void onWriteAccess(Object obj, short value, long field) {
		onWriteAccess(obj, field, value, Type.SHORT);
	}

	@Override
	public void onWriteAccess(Object obj, int value, long field) {
		onWriteAccess(obj, field, value, Type.INT);
	}

	@Override
	public void onWriteAccess(Object obj, long value, long field) {
		onWriteAccess(obj, field, value, Type.LONG);
	}

	@Override
	public void onWriteAccess(Object obj, float value, long field) {
		onWriteAccess(obj, field, value, Type.FLOAT);
	}

	@Override
	public void onWriteAccess(Object obj, double value, long field) {
		onWriteAccess(obj, field, value, Type.DOUBLE);
	}

	@Override
	public void onIrrevocableAccess() {
		// TODO Auto-generated method stub
	}
}
