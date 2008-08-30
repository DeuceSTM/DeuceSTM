package org.deuce.transaction.lsa.field;

import org.deuce.transform.Exclude;

@Exclude
public class ReadFieldAccess extends Field {
	final private int hash;
	final private int lock;

	public ReadFieldAccess(Object reference, long field, int hash, int lock) {
		super(reference, field);
		this.hash = hash;
		this.lock = lock;
	}

	public int getHash() {
		return hash;
	}

	public int getLock() {
		return lock;
	}
}