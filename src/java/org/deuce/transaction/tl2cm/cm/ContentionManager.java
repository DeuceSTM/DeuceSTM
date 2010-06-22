package org.deuce.transaction.tl2cm.cm;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transaction.tl2cm.field.ReadFieldAccess;
import org.deuce.transaction.tl2cm.field.WriteFieldAccess;
import org.deuce.transform.Exclude;

/**
 * This interface describes a Contention Manager object. A Contention Manager object
 * is in-charge of resolving conflicts between threads.
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 * @since 1.2
 */
@Exclude
public interface ContentionManager {

	/**
	 * Action to be taken by the thread calling a Contention Manager
	 */
	public enum Action {
		/**
		 * Restart the current transaction
		 */
		RESTART, 

		/**
		 * Try to acquire the lock again
		 */
		RETRY,
		
		/**
		 * Try to acquire the lock by force
		 */
		STEAL_LOCK,
		
		/**
		 * Continue running the speculative execution of the code
		 */
		CONTINUE;
	}

	/**
	 * Notifies this contention manager that a new transaction has begun
	 */
	public void init();

	/**
	 * Resolves the conflict between the two threads
	 * @param writeField The resource the threads are contending on
	 * @param me The thread that attempts to acquire the resource
	 * @param other The thread the holds the resource
	 * @return Action to perform by contending thread
	 */
	public Action resolveWriteConflict(WriteFieldAccess writeField, Context me, Context other);
	
	
	public Action resolveReadConflict(ReadFieldAccess readField, Context me, Context other);

	/**
	 * Whether or not this contention manager requires the STM system to
	 * collect priorities based on work done by transactions
	 * @return true is this contention manager requires priorities, false otherwise
	 */
	public boolean requiresPriorities();
	
	/**
	 * Whether or not this contention manger requires the STM system to
	 * collect priorities based on killing patterns of transactions
	 * @return true is this contention manager requires priorities, false otherwise
	 */
	public boolean requiresKillPriorities();

	/**
	 * Whether or not this contention manager requires the STM system to
	 * maintain timestamps for each thread
	 * @return true is this contention manager requires timestamps, false otherwise
	 */
	public boolean requiresTimestamps();

	/**
	 * Gets a description of this contention manager
	 * @return description
	 */
	public String getDescription();
	
	
}
