package org.deuce.transaction.swisstm;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.deuce.transaction.swisstm.field.AddressLocks;
import org.deuce.transform.Exclude;

@Exclude
public class LockTable {
	private final ConcurrentMap<Address, AddressLocks> objectsMap;
	
	public LockTable() {
		this.objectsMap = new ConcurrentHashMap<Address, AddressLocks>();
	}
	
	public AddressLocks getLocks(Object obj, long field) {
		Address address = new Address(obj, field);
		return this.objectsMap.putIfAbsent(address, new AddressLocks());
	}
}
