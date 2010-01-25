package org.deuce.transaction.tl2cm.cm;

abstract public class AbstractContentionManager implements ContentionManager {

	@Override
	public boolean requiresPriorities() {
		return false;
	}
	
	@Override
	public boolean requiresTimestamps() {
		return false;
	}

}
