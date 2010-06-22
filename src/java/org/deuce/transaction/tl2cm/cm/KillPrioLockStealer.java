package org.deuce.transaction.tl2cm.cm;

import org.deuce.transaction.tl2cm.cm.ContentionManager.Action;
import org.deuce.transaction.tl2cm.field.ReadFieldAccess;
import org.deuce.transaction.tl2cm.field.WriteFieldAccess;
import org.deuce.transaction.tl2cm.Context;
import org.deuce.transform.Exclude;

@Exclude
public class KillPrioLockStealer extends AbstractContentionManager {

	@Override
	public Action resolveReadConflict(ReadFieldAccess readField, Context me, Context other) {
		int statusRecord = other.getStatusRecord();
		int myPrio = me.getKillPriority();
		int otherPrio = other.getKillPriority();
		if (myPrio >= otherPrio) {
			if (Context.getTxStatus(statusRecord) == Context.TX_ABORTED) {
				return Action.CONTINUE;
			}
			else if (other.kill(Context.getTxLocalClock(statusRecord))) {
				me.increaseKillPriority(otherPrio+1);
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
		int myPrio = me.getKillPriority();
		int otherPrio = other.getKillPriority();
		if (myPrio >= otherPrio) {
			// It is not allowed to steal a lock before the other transaction is aborted
			if (Context.getTxStatus(statusRecord) == Context.TX_ABORTED) {
				return Action.STEAL_LOCK;
			}
			else if (other.kill(Context.getTxLocalClock(statusRecord))) {
				me.increaseKillPriority(otherPrio+1);
				return Action.STEAL_LOCK;
			}
			else {
				me.kill(-1);
				return Action.RESTART;
			}
		}
		return Action.RETRY;
	}
	
	public boolean requiresKillPriorities() {
		return true;
	}
	
	public String getDescription() {
		return "KillPrioLockStealer";
	}

}
