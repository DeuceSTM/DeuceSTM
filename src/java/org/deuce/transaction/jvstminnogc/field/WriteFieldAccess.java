package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
abstract public class WriteFieldAccess extends ReadFieldAccess{

	/**
	 * Commits the value in memory.
	 */
	abstract public void put(int txNumber);
}
