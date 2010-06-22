package org.deuce.transaction.tl2cm.cm;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transaction.tl2cm.field.ReadFieldAccess;

abstract public class AbstractContentionManager implements ContentionManager {

	@Override
	public Action resolveReadConflict(ReadFieldAccess readField, Context me, Context other) {
		return Action.RESTART;
	}
	
	@Override
	public boolean requiresPriorities() {
		return false;
	}
	
	@Override
	public boolean requiresTimestamps() {
		return false;
	}
	
	@Override
	public boolean requiresKillPriorities() {
		return false;
	}
	
	@Override
	public void init() {
		
	}

}
