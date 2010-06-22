package org.deuce.transaction.tl2cm.cm;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transaction.tl2cm.field.ReadFieldAccess;
import org.deuce.transaction.tl2cm.field.WriteFieldAccess;
import org.deuce.transform.Exclude;

@Exclude
public class LockStealer extends AbstractContentionManager {

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
