package org.deuce.transaction.tl2cm.cm;

import org.deuce.transaction.tl2.field.WriteFieldAccess;
import org.deuce.transaction.tl2cm.Context;
import org.deuce.transform.Exclude;

/**
 * The Polka contention manager combines the back-off capabilities of {@code Polite} and the 
 * priority-based resolution algorithm of {@code Karma}.
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 * @since 1.2
 */
@Exclude
public class Polka extends BackoffCM {

	private static int C = 4;
	
	public Polka(int k) {
		C = k;
	}
	
	public Action resolve(WriteFieldAccess contentionPoint, Context contending, Context other) {
		BackoffData myState = getBackoffData();
		int myPrio = contending.getPriority();
		int otherPrio = other.getPriority();
		int myCurrTimestamp = contending.getLocalClock();
		int diff = (myPrio + myState.counter) - otherPrio;
		// Check if the thread is running a new transaction
		// and if so we need to update the thread's state
		if (myState.originalTimestamp < myCurrTimestamp) {
			myState.originalTimestamp = myCurrTimestamp;
			myState.counter = 1;
		}
		else if (diff > 0 && myState.counter > 0) {
			other.kill();
			return Action.RETRY_LOCK;
		}
		
		myState.counter++;
		diff = Math.abs(diff);
		int t = (int) Math.pow(diff, myState.counter) * C;
		for (int i=0; i<t; i++);
		return Action.RETRY_LOCK;
	}
	
	public boolean requiresPriorities() {
		return true;
	}
	
	public String getDescription() {
		return "Polka busy-waiting [C=" + C + "]";
	}

}
