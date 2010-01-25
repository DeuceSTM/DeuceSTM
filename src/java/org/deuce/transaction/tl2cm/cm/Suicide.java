package org.deuce.transaction.tl2cm.cm;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transaction.tl2.field.WriteFieldAccess;
import org.deuce.transform.Exclude;

/**
 * The Suicide contention manager always aborts the transaction of the current thread.
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 * @since 1.2
 */
@Exclude
public class Suicide extends AbstractContentionManager {

	public Action resolve(WriteFieldAccess contentionPoint, Context contending, Context other) {
		return Action.RESTART;
	}

	public String getDescription() {
		return "Suicide";
	}

}
