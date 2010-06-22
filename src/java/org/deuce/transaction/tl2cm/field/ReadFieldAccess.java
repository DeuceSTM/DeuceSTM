package org.deuce.transaction.tl2cm.field;

import org.deuce.transaction.tl2cm.LockTable;
import org.deuce.transform.Exclude;

/**
 * Represents a base class for field write access. Based on Guy Korland's work on <code>org.deuce.transaction.tl2.*</code>
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 */
@Exclude
public class ReadFieldAccess{
	protected Object reference;
	protected long field;
	private int hash;

	public ReadFieldAccess(){}
	
	public ReadFieldAccess(Object reference, long field, int version){
		init(reference, field);
	}
	
	public void init(Object reference, long field){
		this.reference = reference;
		this.field = field;
		this.hash = LockTable.hash(reference, field);
	}

	@Override
	public boolean equals( Object obj){
		ReadFieldAccess other = (ReadFieldAccess)obj;
		return reference == other.reference && field == other.field;
	}

	@Override
	final public int hashCode(){
		return hash;
	}

	public void clear(){
		reference = null;
	}
}