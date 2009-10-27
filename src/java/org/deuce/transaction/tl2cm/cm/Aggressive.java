package org.deuce.transaction.tl2cm.cm;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transaction.tl2.field.WriteFieldAccess;
import org.deuce.transform.Exclude;

/**
 * Aggressive contention manager. Its policy is to always abort the
 * other transaction. 
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 * @since 1.2
 */
@Exclude
public class Aggressive implements ContentionManager {

	public Action resolve(WriteFieldAccess contentionPoint, Context contending, Context other) {
		other.kill(other.getLocalClock());
		return Action.RETRY_LOCK;
	}

	public boolean requiresPriorities() {
		return false;
	}

	public boolean requiresTimestamps() {
		return false;
	}

}
