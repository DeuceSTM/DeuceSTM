package org.deuce.transaction.tl2cm.cm;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transaction.tl2cm.field.ReadFieldAccess;
import org.deuce.transaction.tl2cm.field.WriteFieldAccess;
import org.deuce.transform.Exclude;

/**
 * The Kill Priority LockStealer contention manager resolves conflicts exactly the same as Karma LockStelaer. The only difference is 
 * that it uses a different priority mechanism; the priority of a transaction is the number of transactions it killed so far. When a 
 * transaction kills another transaction the killing transaction increments its priority by 1 + # of transaction the killed transaction killed.
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 * @since 1.4
 */
@Exclude
public class KillPrioLS extends AbstractContentionManager {

	@Override
	public Action resolveReadConflict(ReadFieldAccess readField, Context me, Context other) {
		int statusRecord = other.getStatusRecord();
		int myPrio = me.getKillPriority();
		int otherPrio = other.getKillPriority();
		if (myPrio >= otherPrio) {	// If we do > then all priorities will remain 0 since no one will kill
			if (Context.getTxStatus(statusRecord) == Context.TX_ABORTED) {
				return Action.CONTINUE;
			}
			else if (other.kill(Context.getTxLocalClock(statusRecord))) {
				me.changeKillPriority(otherPrio+1);
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
		if (myPrio >= otherPrio) {	// If we do > then all priorities will remain 0 since no one will kill
			// It is not allowed to steal a lock before the other transaction is aborted
			if (Context.getTxStatus(statusRecord) == Context.TX_ABORTED) {
				return Action.STEAL_LOCK;
			}
			else if (other.kill(Context.getTxLocalClock(statusRecord))) {
				me.changeKillPriority(otherPrio+1);
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
