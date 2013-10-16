package org.deuce.transaction.swisstm.field;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.deuce.transaction.swisstm.Address;
import org.deuce.transform.Exclude;

@Exclude
public final class WriteLog {
	private final Map<Address, WriteFieldAccess> writeLog;

	public WriteLog() {
		this.writeLog = new HashMap<Address, WriteFieldAccess>();
	}

	public void clear() {
		this.writeLog.clear();
	}

	public int size() {
		return this.writeLog.size();
	}

	/**
	 * Gets a read-only version of the addresses in the write log
	 * 
	 * @return All the addresses in the write log. Read-only.
	 */
	public Collection<Address> getAddresses() {
		return Collections.unmodifiableCollection(this.writeLog.keySet());
	}

	public void add(Address address, LockPair locks, Object writtenValue, int version) {
		WriteFieldAccess newEntry = new WriteFieldAccess(locks, writtenValue, version);
		this.writeLog.put(address, newEntry);
	}

	public void update(Address address, Object writtenValue) {
		WriteFieldAccess entry = this.writeLog.get(address);
		entry.writtenValue = writtenValue;
	}

	public Object get(Address address) {
		WriteFieldAccess logEntry = this.writeLog.get(address);
		return logEntry.writtenValue;
	}

	public void lockReadLocks() {
		for (Address address : this.writeLog.keySet()) {
			WriteFieldAccess writeLogEntry = this.writeLog.get(address);
			writeLogEntry.locks.lockRLock();
		}
	}

	/**
	 * Unlocks all the read locks and restores their version to
	 * the value they had when they were added to the write log.
	 */
	public void restoreReadLocks() {
		for (Address address : this.writeLog.keySet()) {
			WriteFieldAccess writeLogEntry = this.writeLog.get(address);
			writeLogEntry.locks.unlockRLock(writeLogEntry.version);
		}
	}

	public void unlockWriteLocks() {
		for (Address address : this.writeLog.keySet()) {
			WriteFieldAccess writeLogEntry = this.writeLog.get(address);
			writeLogEntry.locks.unlockWLock();
		}
	}

	/**
	 * Writes to memory all the values in the write log, unlocks the
	 * r-locks with the timestamp value and unlocks the w-locks.
	 * 
	 * @param timestamp New value for the r-lock.
	 */
	public void commitValues(int timestamp) {
		for (Address address : this.writeLog.keySet()) {
			WriteFieldAccess writeLogEntry = this.writeLog.get(address);
			Field.putValue(address.object, address.field, writeLogEntry.writtenValue, address.type);
			writeLogEntry.locks.unlockRLock(timestamp);
			writeLogEntry.locks.unlockWLock();
		}
	}
}
