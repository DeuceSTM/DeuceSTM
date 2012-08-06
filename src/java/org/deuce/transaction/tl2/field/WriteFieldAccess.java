package org.deuce.transaction.tl2.field;

import org.deuce.transform.ExcludeInternal;

/**
 * Represents a base class for field write access.
 * 
 * @author Guy Korland
 */
@ExcludeInternal
abstract public class WriteFieldAccess extends ReadFieldAccess {

	/**
	 * Commits the value in memory.
	 */
	abstract public void put();
}
