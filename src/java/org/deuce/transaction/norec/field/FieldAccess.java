package org.deuce.transaction.norec.field;

import org.deuce.transform.Exclude;

/**
 * @author Pascal Felber
 */
@Exclude
public class FieldAccess {

	protected Object reference;
	protected long field;
	private int hash;

	public FieldAccess(){}
	
	protected FieldAccess(Object reference, long field) {
		init(reference, field);
	}
	
	public void init(Object reference, long field){
		this.reference = reference;
		this.field = field;
		this.hash = System.identityHashCode(reference) + (int)field;
	}

	public Object getReference() {
		return reference;
	}

	public long getField() {
		return field;
	}

	public void writeField(){}

	public boolean validate() {
		return false;
	}

	@Override
	public boolean equals(Object o) {
		FieldAccess r = (FieldAccess)o;
		return reference == r.reference && field == r.field;
	}

	@Override
	final public int hashCode() {
		return hash;
	}
}