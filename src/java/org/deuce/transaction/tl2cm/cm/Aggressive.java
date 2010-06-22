package org.deuce.transaction.tl2cm.cm;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transaction.tl2cm.field.ReadFieldAccess;
import org.deuce.transaction.tl2cm.field.WriteFieldAccess;
import org.deuce.transform.Exclude;

/**
 * Aggressive contention manager. Its policy is to always abort the
 * other transaction. 
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 * @since 1.2
 */
@Exclude
public class Aggressive extends AbstractContentionManager {

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

	public Action resolveWriteConflict(WriteFieldAccess writeField, Context me, Context other) {
		int statusRecord = other.getStatusRecord();
		if (Context.getTxStatus(statusRecord) == Context.TX_ABORTED || other.kill(Context.getTxLocalClock(statusRecord))) {
			return Action.RETRY;
		}
		else {
			me.kill(-1);
			return Action.RESTART;
		}
	}

	public String getDescription() {
		return "Aggressive";
	}

}
