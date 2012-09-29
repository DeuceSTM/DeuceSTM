package org.deuce.transaction.mvstm.field;

import org.deuce.transform.ExcludeInternal;

/**
 * Represents a base class for field write access.  
 * @author Guy Korland, Ricardo Dias <ricardo.dias@campus.fct.unl.pt>
 */
@ExcludeInternal
abstract public class WriteFieldAccess extends ReadFieldAccess{

	/**
	 * Commits the value in memory.
	 */
	abstract public void put(int txNumber);
}
