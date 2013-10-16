package org.deuce.transaction.swisstm;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.deuce.transaction.swisstm.field.Field;
import org.deuce.transaction.swisstm.field.LockPair;
import org.deuce.transaction.swisstm.field.WriteFieldAccess;
import org.deuce.transform.Exclude;

@Exclude
public final class WriteSet {
	private final Map<Address, WriteFieldAccess> writeSet;

	public WriteSet() {
		this.writeSet = new HashMap<Address, WriteFieldAccess>();
	}

	public void clear() {
		this.writeSet.clear();
	}

	public int size() {
		return this.writeSet.size();
	}

	/**
	 * Gets a read-only version of the addresses in the write set
	 * 
	 * @return All the addresses in the write set. Read-only.
	 */
	public Collection<Address> getAddresses() {
		return Collections.unmodifiableCollection(this.writeSet.keySet());
	}

	public void add(Address address, LockPair locks, Object writtenValue, int version) {
		WriteFieldAccess newEntry = new WriteFieldAccess(locks, writtenValue, version);
		this.writeSet.put(address, newEntry);
	}

	public void update(Address address, Object writtenValue) {
		WriteFieldAccess entry = this.writeSet.get(address);
		entry.writtenValue = writtenValue;
	}

	public Object get(Address address) {
		WriteFieldAccess writeSetEntry = this.writeSet.get(address);
		return writeSetEntry.writtenValue;
	}

	public void lockReadLocks() {
		for (Address address : this.writeSet.keySet()) {
			WriteFieldAccess writeSetEntry = this.writeSet.get(address);
			writeSetEntry.locks.lockRLock();
		}
	}

	/**
	 * Unlocks all the r-locks and restores their version to
	 * the value they had when they were added to the write set.
	 */
	public void restoreReadLocks() {
		for (Address address : this.writeSet.keySet()) {
			WriteFieldAccess writeSetEntry = this.writeSet.get(address);
			writeSetEntry.locks.unlockRLock(writeSetEntry.version);
		}
	}

	public void unlockWriteLocks() {
		for (Address address : this.writeSet.keySet()) {
			WriteFieldAccess writeSetEntry = this.writeSet.get(address);
			writeSetEntry.locks.unlockWLock();
		}
	}

	/**
	 * Writes to memory all the values in the write set, unlocks the
	 * r-locks with the timestamp value and unlocks the w-locks.
	 * 
	 * @param timestamp New value for the r-lock.
	 */
	public void commitValues(int timestamp) {
		for (Address address : this.writeSet.keySet()) {
			WriteFieldAccess writeSetEntry = this.writeSet.get(address);
			Field.putValue(address.object, address.field, writeSetEntry.writtenValue, address.type);
			writeSetEntry.locks.unlockRLock(timestamp);
			writeSetEntry.locks.unlockWLock();
		}
	}
}
