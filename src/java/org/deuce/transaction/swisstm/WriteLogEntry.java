package org.deuce.transaction.swisstm;

import org.deuce.transaction.swisstm.field.AddressLocks;
import org.deuce.transform.Exclude;

@Exclude
public class WriteLogEntry {
	public final AddressLocks locks;
	public final Object value;

	public WriteLogEntry(AddressLocks locks, Object value) {
		this.locks = locks;
		this.value = value;
	}
}
