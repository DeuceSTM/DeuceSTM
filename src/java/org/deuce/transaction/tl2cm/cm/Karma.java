package org.deuce.transaction.tl2cm.cm;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transaction.tl2cm.field.ReadFieldAccess;
import org.deuce.transaction.tl2cm.field.WriteFieldAccess;
import org.deuce.transform.Exclude;

/**
 * The Karma contention manager resolves conflicts by comparing the priorities of
 * the conflicting threads. For more information regarding priorities see {@link Context.getPriority()}. The
 * thread that is supposed to wait waits for a constant period of time.
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 * @since 1.2
 */
@Exclude
public class Karma extends AbstractContentionManager {

	private static int BACKOFF_PERIOD = (int) Math.pow(10, 4);
	private int counter = 0;
	
	public Karma(int k) {
		BACKOFF_PERIOD = (int) Math.pow(10, k);
	}

	public Action resolveWriteConflict(WriteFieldAccess writeField, Context me, Context other) {
		int myPrio = me.getPriority();
		int otherPrio = other.getPriority();
		if (myPrio + counter > otherPrio) {
			int statusRecord = other.getStatusRecord();
			// It is not allowed to steal a lock before the other transaction is aborted
			if (Context.getTxStatus(statusRecord) == Context.TX_ABORTED || other.kill(Context.getTxLocalClock(statusRecord))) {
				return Action.RETRY;
			}
			else {
				me.kill(-1);
				return Action.RESTART;
			}
		}
		counter++;
		for (int i=0; i<BACKOFF_PERIOD; i++);
		return Action.RETRY;
	}
	 
	@Override
	public Action resolveReadConflict(ReadFieldAccess readField, Context me, Context other) {
		int statusRecord = other.getStatusRecord();
		int myPrio = me.getPriority();
		int otherPrio = other.getPriority();
		if (myPrio > otherPrio) {
			if (Context.getTxStatus(statusRecord) == Context.TX_ABORTED || other.kill(Context.getTxLocalClock(statusRecord))) {
				return Action.CONTINUE;
			}
			else {
				me.kill(-1);
				return Action.RESTART;
			}
		}
		for (int i=0; i<BACKOFF_PERIOD; i++);
		return Action.RETRY;
	}
	
	public boolean requiresPriorities() {
		return true;
	}

	public String getDescription() {
		return "Karma busy-waiting backoff [Backoff=" + BACKOFF_PERIOD + "]";
	}

	@Override
	public void init() {
		counter = 0;
	}

}
