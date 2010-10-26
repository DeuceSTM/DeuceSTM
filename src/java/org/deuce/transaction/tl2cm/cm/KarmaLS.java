package org.deuce.transaction.tl2cm.cm;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transaction.tl2cm.field.ReadFieldAccess;
import org.deuce.transaction.tl2cm.field.WriteFieldAccess;
import org.deuce.transform.Exclude;

/**
 * The Karma LockStealer contention manager resolves conflicts exactly the same as the {@code Karma} contention manager. The only
 * difference is that Karma LockStealer will not suffice with killing the other transaction, it will also attempt to steal its lock.
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 * @since 1.4
 */  
@Exclude
public class KarmaLS extends AbstractContentionManager {

	private int counter = 0;
	
	@Override
	public void init() {
		counter = 0;
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

}
