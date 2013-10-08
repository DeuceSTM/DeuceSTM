package org.deuce.transaction.swisstm;

import org.deuce.transaction.swisstm.field.AddressLocks;
import org.deuce.transform.Exclude;

@Exclude
public class ReadLogEntry {
	public final AddressLocks locks;
	public final int version;

	public ReadLogEntry(AddressLocks locks, int version) {
		this.locks = locks;
		this.version = version;
	}
}
