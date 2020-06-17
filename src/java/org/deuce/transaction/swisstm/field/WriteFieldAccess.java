package org.deuce.transaction.swisstm.field;

import org.deuce.transform.Exclude;

@Exclude
public final class WriteFieldAccess {
	public final LockPair locks;
	public Object writtenValue;
	public final int version;	// version of the r-lock when the write was made

	public WriteFieldAccess(LockPair locks, Object writtenValue, int version) {
		this.locks = locks;
		this.writtenValue = writtenValue;
		this.version = version;
	}
}
