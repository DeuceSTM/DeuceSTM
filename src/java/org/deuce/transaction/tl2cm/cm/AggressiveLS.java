package org.deuce.transaction.tl2cm.cm;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transaction.tl2cm.field.ReadFieldAccess;
import org.deuce.transaction.tl2cm.field.WriteFieldAccess;
import org.deuce.transform.Exclude;

/**
 * A contention manager that is able to revoke a lock from another transaction.<br>
 * When a read conflict is encountered, {@code LockStealer} will attempt to kill the other transaction and if successful will continue
 * with its execution. Otherwise, it aborts.<br>
 * When a write conflict is encountered, {@code LockStealer} will attempt to kill the other transaction and if successful will try to 
 * steal the lock its holding to allow its own execution to continue. Otherwise it aborts.
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 * @since 1.4
 */
@Exclude
public class AggressiveLS extends AbstractContentionManager {

	@Override
	public Action resolveReadConflict(ReadFieldAccess readField, Context me, Context other) {
		int statusRecord = other.getStatusRecord();
		if (Context.getTxStatus(statusRecord) == Context.TX_ABORTED || other.kill(Context.getTxLocalClock(statusRecord))) {
			return Action.CONTINUE;
		}
		else {
			me.kill(-1);
			return Action.RESTART;
		}
	}
	
	@Override
	public Action resolveWriteConflict(WriteFieldAccess writeField, Context me,	Context other) {
		int statusRecord = other.getStatusRecord();
		// It is not allowed to steal a lock before the other transaction is aborted
		if (Context.getTxStatus(statusRecord) == Context.TX_ABORTED || other.kill(Context.getTxLocalClock(statusRecord))) {
			return Action.STEAL_LOCK;
		}
		else {
			me.kill(-1);
			return Action.RESTART;
		}
	}
	
	@Override
	public String getDescription() {
		return "LockStealer";
	}


}
