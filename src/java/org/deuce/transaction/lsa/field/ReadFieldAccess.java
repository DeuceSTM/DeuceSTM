package org.deuce.transaction.lsa.field;

import org.deuce.transform.Exclude;

/**
 * @author Pascal Felber
 */
@Exclude
public class ReadFieldAccess {

	private Object reference;
	private long field;
	private int hash;
	private int lock;

	public ReadFieldAccess() { }

	public ReadFieldAccess(Object reference, long field, int hash, int lock) {
		this.reference = reference;
		this.field = field;
		this.hash = hash;
		this.lock = lock;
	}

	public void init(Object reference, long field, int hash, int lock) {
		this.reference = reference;
		this.field = field;
		this.hash = hash;
		this.lock = lock;
	}

	public Object getReference() {
		return reference;
	}

	public long getField() {
		return field;
	}

	public int getHash() {
		return hash;
	}

	public int getLock() {
		return lock;
	}
}