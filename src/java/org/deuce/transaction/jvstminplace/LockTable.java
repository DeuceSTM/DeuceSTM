package org.deuce.transaction.jvstminplace;


import org.deuce.transaction.TransactionException;
import org.deuce.transform.ExcludeInternal;


@ExcludeInternal
public class LockTable {

	// Failure transaction 
	final public static TransactionException LOCKED_VERSION_EXCEPTION = new TransactionException( "Faild on locking version.");
	final public static int LOCK = 1 << 31;
	final public static int UNLOCK = ~LOCK;
	

}
