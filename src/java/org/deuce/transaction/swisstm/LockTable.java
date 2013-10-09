package org.deuce.transaction.swisstm;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.deuce.transaction.swisstm.field.AddressLocks;
import org.deuce.transaction.swisstm.field.Field.Type;
import org.deuce.transform.Exclude;

@Exclude
public class LockTable {
	private final ConcurrentMap<Address, AddressLocks> objectsMap;

	public LockTable() {
		this.objectsMap = new ConcurrentHashMap<Address, AddressLocks>();
	}

	public AddressLocks getLocks(Object obj, long field, Type type) {
		Address address = new Address(obj, field, type);
		AddressLocks newValue = new AddressLocks();
		AddressLocks previousValue = this.objectsMap.putIfAbsent(address, newValue);
		return previousValue == null ? newValue : previousValue;
	}
}
