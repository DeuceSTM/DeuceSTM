package org.deuce.transaction.tl2cm.cm;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transaction.tl2.field.WriteFieldAccess;
import org.deuce.transform.Exclude;

@Exclude
public class Timestamp extends BackoffCM {

	//private static final Logger logger = Logger.getLogger(Context.TL2CM_LOGGER);
	private static int BACKOFF_PERIOD = (int) Math.pow(10, 4);
	
	public Timestamp(int k) {
		BACKOFF_PERIOD = (int) Math.pow(10, k);
	}

	public Action resolve(WriteFieldAccess contentionPoint, Context contending, Context other) {
		int myTimestamp = contending.getTimestamp();
		int otherTimestamp = other.getTimestamp();
		BackoffData myState = getBackoffData();
		int currTxClock = contending.getLocalClock();
		// Check if the thread is running a new transaction
		// and if so we need to update the thread's state
		if (myState.originalTimestamp < currTxClock) {
			myState.originalTimestamp = currTxClock;
			myState.counter = 1;
		}
		else if (myTimestamp > otherTimestamp) {
			other.kill();
			return Action.RETRY_LOCK;
		}
		// increase back-off counter and loop until
		// the time comes to retry	
		//logger.log(Level.WARNING, "TID={0} performing backoff #{1}", new Object[]{contending.getThreadId(), myState.counter});
		try {
			Thread.sleep(0, BACKOFF_PERIOD);
		} 
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myState.counter++;
		return Action.RETRY_LOCK;
	}
	
	public boolean requiresTimestamps() {
		return true;
	}

	public String getDescription() {
		return "Timestamp busy-waiting backoff [Backoff=" + BACKOFF_PERIOD + "]";
	}

}
