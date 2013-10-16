package org.deuce.transaction.swisstm;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.deuce.transaction.swisstm.field.LockPair;
import org.deuce.transform.Exclude;

/**
 * Maps each Address to a pair of a w-lock and a r-lock
 */
@Exclude
public final class LockTable {

	private final ConcurrentMap<Address, LockPair> objectsMap;

	public LockTable() {
		this.objectsMap = new ConcurrentHashMap<Address, LockPair>();
	}

	public LockPair getLocks(Address address) {
		LockPair newValue = new LockPair();
		LockPair previousValue = this.objectsMap.putIfAbsent(address, newValue);
		return previousValue == null ? newValue : previousValue;
	}
}
