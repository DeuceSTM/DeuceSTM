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
	 * Notifies this contention manager to clear any internal bookkeeping it might keep
	 */
	public void init();

	/**
	 * Resolves the conflict that is caused when {@code me} tries to read the shared object that 
	 * is represented by {@code readField} while at the same time {@code other} is holding
	 * the lock that is associated with that object.
	 * @param readField object descriptor
	 * @param me the context of the thread that attempts to read the shared object
	 * @param other the context of the thread that is holding the lock associated with the shared object
	 * @return Action to perform by calling thread
	 */
	public Action resolveReadConflict(ReadFieldAccess readField, Context me, Context other);
	
	/**
	 * Resolves the conflict that is caused when {@code me} tries to write to the shared object that
	 * is represented by {@code writeField} while at the same time {@code other} is holding 
	 * the lock that is associated with that object.
	 * @param writeField object descriptor
	 * @param me the context of the thread that attempts to write to the shared object
	 * @param other the context of the thread that is holding the lock associated with the shared object
	 * @return Action to perform by calling thread
	 */
	public Action resolveWriteConflict(WriteFieldAccess writeField, Context me, Context other);
	
	/**
	 * Whether or not this contention manager requires the STM system to
	 * collect priorities based on how many objects were accessed by the current transaction
	 * @return true is this contention manager requires priorities, false otherwise
	 */
	public boolean requiresPriorities();
	
	/**
	 * Whether or not this contention manger requires the STM system to
	 * collect priorities based on how many transactions were killed by the current transaction
	 * @return true is this contention manager requires priorities, false otherwise
	 */
	public boolean requiresKillPriorities();

	/**
	 * Gets a description of this contention manager
	 * @return description
	 */
	public String getDescription();
	
	
}
