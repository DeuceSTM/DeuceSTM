package org.deuce.transaction.tl2cm;

import org.deuce.transform.Exclude;

@Exclude
public enum Phase {

	/**
	 * The transaction is running a speculative execution of the code,
	 * collecting read and write sets and eventually will try to commit.
	 */
	RUNNING,
	
	/**
	 * The transaction has finished committing and the thread hasn't 
	 * started a new transaction yet.
	 */
	COMMITTED,
	
	/**
	 * The transaction has aborted and the thread hasn't started a new
	 * transaction yet.
	 */
	ABORTED,
	
	/**
	 * Indicates that the thread is no longer active and will not
	 * execute another transaction. 
	 */
	INACTIVE
	
}
