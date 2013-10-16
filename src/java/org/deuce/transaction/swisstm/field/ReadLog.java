package org.deuce.transaction.swisstm.field;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.deuce.transaction.swisstm.Address;
import org.deuce.transform.Exclude;

@Exclude
public final class ReadLog {
	private final Map<Address, ReadFieldAccess> readLog;

	public ReadLog() {
		this.readLog = new HashMap<Address, ReadFieldAccess>();
	}

	public void clear() {
		this.readLog.clear();
	}

	/**
	 * Validates all the read locations.
	 * A location is valid if the version when it was first read matches the
	 * current version of the r-lock.
	 * 
	 * @param excludedAddresses
	 * @return true if all read locations are valid, false otherwise
	 */
	public boolean validate() {
		return validate(Collections.<Address> emptySet());
	}

	/**
	 * Validates all the read locations except for the ones on excludedAddresses.
	 * A location is valid if the version when it was first read matches the
	 * current version of the r-lock.
	 * 
	 * @param excludedAddresses
	 * @return true if all read locations that are not in excludedAddresses
	 *         are valid, false otherwise
	 */
	public boolean validate(Collection<Address> excludedAddresses) {
		for (Address address : this.readLog.keySet()) {
			ReadFieldAccess readLogEntry = this.readLog.get(address);
			boolean wasEntryChanged = readLogEntry.version != readLogEntry.locks.getRLockVersion();
			boolean isExcluded = excludedAddresses.contains(address);
			if (wasEntryChanged && !isExcluded) {
				return false;
			}
		}
		return true;
	}

	public void add(Address address, LockPair locks, int version) {
		ReadFieldAccess newEntry = new ReadFieldAccess(locks, version);
		this.readLog.put(address, newEntry);
	}
}
