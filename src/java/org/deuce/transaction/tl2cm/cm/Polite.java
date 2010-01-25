package org.deuce.transaction.tl2cm.cm;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transaction.tl2.field.WriteFieldAccess;
import org.deuce.transform.Exclude;

/**
 * The Polite contention manager resolves conflicts by backing off and retrying the lock. 
 * If after {@code MAX_BACKOFF_TIMES} the lock cannot be acquired, the other thread's 
 * transaction is aborted.
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 * @since 1.2
 */
@Exclude
public class Polite extends BackoffCM {
	
	private static final Logger logger = Logger.getLogger(Context.TL2CM_LOGGER);
	private static final int MAX_BACKOFF_TIMES = 22;
	private static int K = 4;
	
	public Polite(int k) {
		K = k;
	}

	public Action resolve(WriteFieldAccess contentionPoint, Context contending, Context other) {
		BackoffData myState = getBackoffData();
		int currTxTimestamp = contending.getLocalClock();
		if (myState.originalTimestamp < currTxTimestamp) {
			// The thread is running a new transaction
			myState.originalTimestamp = currTxTimestamp;
			myState.counter = 1;
		} 
		else if (myState.counter == MAX_BACKOFF_TIMES) {
			// The thread is not allowed to back-off any longer
			other.kill();
			return Action.RETRY_LOCK;
		}
		// increase back-off counter and loop until
		// the time comes to retry
		int timeToWait = calculateTimeToWait(myState.counter);
		for (int i=0; i<timeToWait; i++);
		logger.log(Level.INFO, "TID={0} performing backoff #{1} of {2}, ", new Object[]{contending.getThreadId(), myState.counter, timeToWait});
		myState.counter++;
		return Action.RETRY_LOCK;
	}

	private int calculateTimeToWait(int n) { 
		int t = (int) Math.pow(2, n+K); 
		return t;
	}

	public String getDescription() {
		return "Polite busy-waiting [K=" + K + "Max backoff=" + MAX_BACKOFF_TIMES + "]";
	}

}
