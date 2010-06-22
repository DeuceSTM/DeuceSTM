package org.deuce.transaction.tl2cm.cm;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transaction.tl2cm.field.ReadFieldAccess;
import org.deuce.transaction.tl2cm.field.WriteFieldAccess;
import org.deuce.transform.Exclude;

/**
 * The Karma contention manager resolves conflicts by comparing the priorities of
 * the conflicting threads. For more information regarding priorities see {@link org.deuce.transaction.tl2cm.Context#getPriority()}. The
 * thread that is supposed to wait waits for a constant period of time.
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 * @since 1.2
 */  
@Exclude
public class KarmaLockStealer extends AbstractContentionManager {

	private int counter = 0;
	
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
		return Action.RESTART;
	}
	
	public Action resolveWriteConflict(WriteFieldAccess writeField, Context me, Context other) {
		int statusRecord = other.getStatusRecord();
		int myPrio = me.getPriority();
		int otherPrio = other.getPriority();
		if (myPrio + counter > otherPrio) {
			// It is not allowed to steal a lock before the other transaction is aborted
			if (Context.getTxStatus(statusRecord) == Context.TX_ABORTED || other.kill(Context.getTxLocalClock(statusRecord))) {
				return Action.STEAL_LOCK;
			}
			else {
				me.kill(-1);
				return Action.RESTART;
			}
		}
		counter++;
		return Action.RETRY;
	}
	
	public boolean requiresPriorities() {
		return true;
	}
	
	public String getDescription() {
		return "KarmaLockStealer";
	}

	@Override
	public void init() {
		counter = 0;
	}

}
