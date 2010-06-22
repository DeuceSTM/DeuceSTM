package org.deuce.transaction.tl2cm.cm;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transaction.tl2cm.field.ReadFieldAccess;
import org.deuce.transaction.tl2cm.field.WriteFieldAccess;
import org.deuce.transform.Exclude;

/**
 * The Suicide contention manager always aborts the current transaction
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 * @since 1.2
 */
@Exclude
public class Suicide extends AbstractContentionManager {

	@Override
	public Action resolveReadConflict(ReadFieldAccess readField, Context me, Context other) {
		me.kill(-1);
		return Action.RESTART;
	}

	public Action resolveWriteConflict(WriteFieldAccess writeField, Context me, Context other) {
		me.kill(-1);
		return Action.RESTART;
	}

	public String getDescription() {
		return "Suicide";
	}

}
