package org.deuce.transaction.tl2cm.cm;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transaction.tl2cm.field.ReadFieldAccess;
import org.deuce.transaction.tl2cm.field.WriteFieldAccess;
import org.deuce.transform.Exclude;

/**
 * The Polka contention manager combines the back-off capabilities of {@code Polite} and the 
 * priority-based resolution algorithm of {@code Karma}.
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 * @since 1.2
 */
@Exclude
public class Polka extends AbstractContentionManager {

	private static int C = 4;
	private int counter = 0;
	
	public Polka(int k) {
		C = k;
	}
	
	@Override
	public void init() {
		counter = 0;
	}

	@Override
	public Action resolveReadConflict(ReadFieldAccess readField, Context me, Context other) {
		int statusRecord = other.getStatusRecord();
		int myPrio = me.getPriority();
		int otherPrio = other.getPriority();
		int diff = (myPrio + counter) - otherPrio;
		if (diff > 0 && counter > 0) {
			if (Context.getTxStatus(statusRecord) == Context.TX_ABORTED || other.kill(Context.getTxLocalClock(statusRecord))) {
				return Action.CONTINUE;
			}
			else {
				me.kill(-1);
				return Action.RESTART;
			}
		}
		counter++;
		diff = Math.abs(diff);
		int t = (int) Math.pow(diff, counter) * C;
		for (int i=0; i<t; i++);
		return Action.RETRY;
	}

	public Action resolveWriteConflict(WriteFieldAccess writeField, Context me, Context other) {
		int myPrio = me.getPriority();
		int otherPrio = other.getPriority();
		int diff = (myPrio + counter) - otherPrio;
		if (diff > 0 && counter > 0) {
			int statusRecord = other.getStatusRecord();
			if (Context.getTxStatus(statusRecord) == Context.TX_ABORTED || other.kill(Context.getTxLocalClock(statusRecord))) {
				return Action.RETRY;
			}
			else {
				me.kill(-1);
				return Action.RESTART;
			}
		}
		counter++;
		diff = Math.abs(diff);
		int t = (int) Math.pow(diff, counter) * C;
		for (int i=0; i<t; i++);
		return Action.RETRY;
	}
	
	public boolean requiresPriorities() {
		return true;
	}
	
	public String getDescription() {
		return "Polka busy-waiting [C=" + C + "]";
	}

}
