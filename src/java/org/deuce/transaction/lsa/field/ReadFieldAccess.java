package org.deuce.transaction.lsa.field;

import org.deuce.transform.Exclude;

/**
 * @author Pascal Felber
 * @since 0.1
 */
@Exclude
public class ReadFieldAccess extends Field {
	private int hash;
	private int lock;

	ReadFieldAccess() { }

	public ReadFieldAccess(Object reference, long field, int hash, int lock) {
		super(reference, field);
		this.hash = hash;
		this.lock = lock;
	}

	public void init(Object reference, long field, int hash, int lock) {
		super.init(reference, field);
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