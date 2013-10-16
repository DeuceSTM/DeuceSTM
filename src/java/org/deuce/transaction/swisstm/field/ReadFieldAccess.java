package org.deuce.transaction.swisstm.field;

import org.deuce.transform.Exclude;

@Exclude
public final class ReadFieldAccess {
	public final LockPair locks;
	public final int version;	// version of the r-lock when the read was made

	public ReadFieldAccess(LockPair locks, int version) {
		this.locks = locks;
		this.version = version;
	}
}
