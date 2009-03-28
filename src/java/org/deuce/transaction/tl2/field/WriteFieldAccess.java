package org.deuce.transaction.tl2.field;

import org.deuce.transform.Exclude;

/**
 * Represents a base class for field write access.  
 * @author Guy Koralnd
 */
@Exclude
abstract public class WriteFieldAccess extends ReadFieldAccess{

	/**
	 * Commits the value in memory.
	 */
	abstract public void put();

	public void init(Object reference, long field) {
		super.init(reference, field);
	}
}
