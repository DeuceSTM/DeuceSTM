package org.deuce.transaction.swisstm;

import org.deuce.transaction.swisstm.field.AddressLocks;
import org.deuce.transform.Exclude;

@Exclude
public class WriteLogEntry {
	public final AddressLocks locks;
	public Object value;
	public final int version;

	public WriteLogEntry(AddressLocks locks, Object value, int version) {
		this.locks = locks;
		this.value = value;
		this.version = version;
	}
}
