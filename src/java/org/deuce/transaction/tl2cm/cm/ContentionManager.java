package org.deuce.transaction.tl2cm.cm;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transaction.tl2.field.WriteFieldAccess;
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
	 * Action to be taken by the thread calling a contention manager
	 */
	public enum Action {
		/**
		 * Restart the current transaction
		 */
		RESTART, 

		/**
		 * Retry the lock
		 */
		RETRY_LOCK,
		
		/**
		 * Attempt to take the lock even if it is locked
		 */
		STEAL_LOCK, 
		
		SKIP_LOCK
	}

	/**
	 * Resolves the conflict between the two threads
	 * @param contentionPoint The resource the threads are contending on
	 * @param contending The thread that attempts to acquire the resource
	 * @param other The thread the holds the resource
	 * @return Action to perform by contending thread
	 */
	public Action resolve(WriteFieldAccess contentionPoint, Context contending, Context other);

	/**
	 * Whether or not this contention manager requires the STM system to
	 * collect priorities for each thread
	 * @return true is this contention manager requires priorities, false otherwise
	 */
	public boolean requiresPriorities();
	
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
