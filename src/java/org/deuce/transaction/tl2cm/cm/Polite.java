package org.deuce.transaction.tl2cm.cm;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transaction.tl2cm.field.ReadFieldAccess;
import org.deuce.transaction.tl2cm.field.WriteFieldAccess;
import org.deuce.transform.Exclude;

/**
 * The Polite contention manager resolves conflicts by backing off and retrying the lock. 
 * If after {@code MAX_BACKOFF_TIMES} the lock cannot be acquired the current transaction tries to abort the other transaction.
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 * @since 1.2
 */
@Exclude
public class Polite extends AbstractContentionManager {
	
	private static final int MAX_BACKOFF_TIMES = 22;
	private static int K = 4;
	private int counter = 0;
	
	public Polite(int k) {
		K = k;
	}

	@Override
	public void init() {
		counter = 0;
	}

	@Override
	public Action resolveReadConflict(ReadFieldAccess readField, Context me, Context other) {
		if (counter == MAX_BACKOFF_TIMES) {
			// The thread is not allowed to back-off any longer
			int statusRecord = other.getStatusRecord();
			if (Context.getTxStatus(statusRecord) == Context.TX_ABORTED || other.kill(Context.getTxLocalClock(statusRecord))) {
				return Action.CONTINUE;
			}
			else {
				me.kill(-1);
				return Action.RESTART;
			}
		}
		// increase back-off counter and loop until the time comes to retry
		int timeToWait = calculateTimeToWait(counter);
		for (int i=0; i<timeToWait; i++);
		counter++;
		return Action.RETRY;
	}

	public Action resolveWriteConflict(WriteFieldAccess writeField, Context me, Context other) {
		if (counter == MAX_BACKOFF_TIMES) {
			// The thread is not allowed to back-off any longer
			int statusRecord = other.getStatusRecord();
			if (Context.getTxStatus(statusRecord) == Context.TX_ABORTED || other.kill(Context.getTxLocalClock(statusRecord))) {
				return Action.RETRY;
			}
			else {
				me.kill(-1);
				return Action.RESTART;
			}
		}
		// increase back-off counter and loop until
		// the time comes to retry
		int timeToWait = calculateTimeToWait(counter);
		for (int i=0; i<timeToWait; i++);
		counter++;
		return Action.RETRY;
	}

	public String getDescription() {
		return "Polite busy-waiting [K=" + K + "Max backoff=" + MAX_BACKOFF_TIMES + "]";
	}

	private int calculateTimeToWait(int n) { 
		int t = (int) Math.pow(2, n+K); 
		return t;
	}
	
}
