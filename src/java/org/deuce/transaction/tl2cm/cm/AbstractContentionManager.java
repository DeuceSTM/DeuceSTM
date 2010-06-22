package org.deuce.transaction.tl2cm.cm;

/**
 * This class provides default implementation for contention managers
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 * @since 1.2
 */
abstract public class AbstractContentionManager implements ContentionManager {

	@Override
	public boolean requiresPriorities() {
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
