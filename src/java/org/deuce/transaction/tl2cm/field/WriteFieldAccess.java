package org.deuce.transaction.tl2cm.field;

import org.deuce.transform.Exclude;

/**
 * Represents a base class for field write access. Based on Guy Korland's work on <code>org.deuce.transaction.tl2.*</code>
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 */
@Exclude
abstract public class WriteFieldAccess extends ReadFieldAccess{

	/**
	 * Commits the value in memory.
	 */
	abstract public void put();
}
