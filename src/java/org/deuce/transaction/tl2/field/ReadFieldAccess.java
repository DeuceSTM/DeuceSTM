package org.deuce.transaction.tl2.field;

import org.deuce.transform.Exclude;

@Exclude
public class ReadFieldAccess{
	final protected Object reference;
	final protected long field;
	final private int hash;

	public ReadFieldAccess( Object reference, long field){
		this.reference = reference;
		this.field = field;
		this.hash = hash(reference, field);
	}

	@Override
	public boolean equals( Object obj){
		ReadFieldAccess other = (ReadFieldAccess)obj;
		return reference == other.reference && field == other.field;
	}

	@Override
	public int hashCode(){
		return hash;
	}

	private static int hash(Object obj, long field){
		return System.identityHashCode( obj) + (int)field;
	}
}