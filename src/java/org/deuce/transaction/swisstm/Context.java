package org.deuce.transaction.swisstm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.deuce.transaction.TransactionException;
import org.deuce.transaction.swisstm.field.AddressLocks;
import org.deuce.transaction.swisstm.field.Field;
import org.deuce.transaction.swisstm.field.Field.Type;
import org.deuce.transform.Exclude;

/**
 * SwissTM implementation
 *
 * TODO:
 *  - RO_HINT
 *  - Contention Manager
 *
 * @author Daniel Pinto
 */
@Exclude
public final class Context implements org.deuce.transaction.Context {

	// Exceptions
	private static final TransactionException WRITE_FAILURE_EXCEPTION =
			new TransactionException("Fail on write.");

	private static final TransactionException READ_FAILURE_EXCEPTION =
			new TransactionException("Fail on read.");

	// Global lock used to allow only one irrevocable transaction solely.
	private final static ReentrantReadWriteLock irrevocableAccessLock =
			new ReentrantReadWriteLock();
	private boolean irrevocableState = false;

	// Global variables
	private static final AtomicInteger threadID = new AtomicInteger(1);
	private static final AtomicInteger commitTS = new AtomicInteger(0);
	private static final LockTable lockTable = new LockTable();

	// Transaction local variables
	private final int id;
	private int validTS;
	private final Map<Address, ReadLogEntry> readLog;
	private final Map<Address, WriteLogEntry> writeLog;
	private final Set<Address> readLockedAddresses;

	public Context() {
		this.readLog = new HashMap<Address, ReadLogEntry>();
		this.writeLog = new HashMap<Address, WriteLogEntry>();
		this.readLockedAddresses = new HashSet<Address>();
		this.id = threadID.incrementAndGet();
	}

	@Override
	public void init(int atomicBlockId, String metainf) {
		// Lock according to the transaction irrevocable state
		if (this.irrevocableState) {
			irrevocableAccessLock.writeLock().lock();
		} else {
			irrevocableAccessLock.readLock().lock();
		}

		// Clear logs
		this.readLog.clear();
		this.writeLog.clear();
		this.readLockedAddresses.clear();
		this.validTS = commitTS.get();
		// TODO: cm-start(tx)
	}

	private Object onReadAccess(Object obj, long field, Type type) {
		AddressLocks locks = lockTable.getLocks(obj, field, type);
		if (locks.getWLockThreadID() == this.id) { // Locked by me?
			return getFromWriteLog(obj, field, type);
		}

		// Get a consistent reading of the value
		Object value;
		int version = locks.getRLockVersion();
		int version2;
		while (true) {
			if (version == AddressLocks.READ_LOCKED) {
				version = locks.getRLockVersion();
				continue;
			}
			value = Field.getValue(obj, field, type);
			version2 = locks.getRLockVersion();
			if (version == version2) {
				break;
			}
			version2 = version;
		}

		addToReadLog(obj, field, type, locks, version);
		if (version > this.validTS && !extend()) {
			// Is calling rollback needed???
			throw READ_FAILURE_EXCEPTION;
		}

		return value;
	}

	private void onWriteAccess(Object obj, long field, Object value, Type type) {
		AddressLocks locks = lockTable.getLocks(obj, field, type);
		if (locks.getWLockThreadID() == this.id) { // Locked by me?
			addToWriteLog(obj, field, type, locks, value);
			return;
		}

		while (true) {
			if (locks.getWLockThreadID() != AddressLocks.WRITE_UNLOCKED) {
				// TODO
				// if cm-should-abort(tx, w-lock) then rollback()
				// else continue

				throw WRITE_FAILURE_EXCEPTION;
			}

			addToWriteLog(obj, field, type, locks, value);

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
	public boolean commit() {
		try {
			if (isReadOnly()) {
				return true;
			}

			// Lock r-locks of read addresses
			for (Address address : this.readLog.keySet()) {
				ReadLogEntry readLogEntry = this.readLog.get(address);
				readLogEntry.locks.lockRLock();
				this.readLockedAddresses.add(address);
			}

			int ts = commitTS.incrementAndGet();
			if (ts > this.validTS + 1 && !validate()) {
				for (Address address : this.readLog.keySet()) {
					ReadLogEntry readLogEntry = this.readLog.get(address);
					readLogEntry.locks.unlockRLock(readLogEntry.version);
				}

				// rollback call needed???
				return false;
			}

			for (Address address : this.writeLog.keySet()) {
				WriteLogEntry writeLogEntry = this.writeLog.get(address);
				Field.putValue(address.object, address.field, writeLogEntry.value, address.type);
				writeLogEntry.locks.unlockRLock(ts);
				writeLogEntry.locks.unlockWLock();
			}
			return true;
		} finally {
			if (this.irrevocableState){
				this.irrevocableState = false;
				irrevocableAccessLock.writeLock().unlock();
			} else {
				irrevocableAccessLock.readLock().unlock();
			}
		}
	}

	@Override
	public void rollback() {
		try {
			for (Address address : this.writeLog.keySet()) {
				WriteLogEntry writeLogEntry = this.writeLog.get(address);
				writeLogEntry.locks.unlockWLock();
			}
			// TODO: cm-on-rollback()
		} finally {
			irrevocableAccessLock.readLock().unlock();
		}
	}

	private boolean validate() {
		for (Address address : this.readLog.keySet()) {
			ReadLogEntry readLogEntry = this.readLog.get(address);
			boolean wasEntryChanged = readLogEntry.version != readLogEntry.locks.getRLockVersion();
			boolean isLockedByMe = this.readLockedAddresses.contains(address);
			if (wasEntryChanged && !isLockedByMe) {
				return false;
			}
		}
		return true;
	}

	private boolean extend() {
		int ts = commitTS.get();
		if (validate()) {
			this.validTS = ts;
			return true;
		}
		return false;
	}

	private void addToReadLog(Object obj, long field, Type type, AddressLocks locks, int version) {
		Address address = new Address(obj, field, type);
		ReadLogEntry newEntry = new ReadLogEntry(locks, version);
		this.readLog.put(address, newEntry);
	}

	private void addToWriteLog(Object obj, long field, Type type, AddressLocks locks, Object value) {
		Address address = new Address(obj, field, type);
		WriteLogEntry newEntry = new WriteLogEntry(locks, value);
		this.writeLog.put(address, newEntry);
	}

	private Object getFromWriteLog(Object obj, long field, Type type) {
		Address address = new Address(obj, field, type);
		return this.writeLog.get(address);
	}

	private boolean isReadOnly() {
		return this.writeLog.size() == 0;
	}

	@Override
	public Object onReadAccess(Object obj, Object value, long field) {
		return onReadAccess(obj, field, Type.OBJECT);
	}

	@Override
	public boolean onReadAccess(Object obj, boolean value, long field) {
		return (Boolean) onReadAccess(obj, field, Type.BOOLEAN);
	}

	@Override
	public byte onReadAccess(Object obj, byte value, long field) {
		return ((Number) onReadAccess(obj, field, Type.BYTE)).byteValue();
	}

	@Override
	public char onReadAccess(Object obj, char value, long field) {
		return (Character) onReadAccess(obj, field, Type.CHAR);
	}

	@Override
	public short onReadAccess(Object obj, short value, long field) {
		return ((Number) onReadAccess(obj, field, Type.SHORT)).shortValue();
	}

	@Override
	public int onReadAccess(Object obj, int value, long field) {
		return ((Number) onReadAccess(obj, field, Type.INT)).intValue();
	}

	@Override
	public long onReadAccess(Object obj, long value, long field) {
		return ((Number) onReadAccess(obj, field, Type.LONG)).longValue();
	}

	@Override
	public float onReadAccess(Object obj, float value, long field) {
		return ((Number) onReadAccess(obj, field, Type.FLOAT)).floatValue();
	}

	@Override
	public double onReadAccess(Object obj, double value, long field) {
		return ((Number) onReadAccess(obj, field, Type.DOUBLE)).doubleValue();
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
	public void beforeReadAccess(Object obj, long field) {
		// Useless for SwissTM
	}

	@Override
	public void onIrrevocableAccess() {
		if (this.irrevocableState) { // Already in irrevocable state so no need to restart transaction
			return;
		}

		this.irrevocableState = true;
		throw TransactionException.STATIC_TRANSACTION;
	}
}
