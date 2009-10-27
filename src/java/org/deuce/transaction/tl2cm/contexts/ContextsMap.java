package org.deuce.transaction.tl2cm.contexts;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transform.Exclude;

/**
* @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
* @since 1.2
*/
@Exclude
public interface ContextsMap {

	public Context get(int threadId);
	
	public void put(int threadId, Context context);
	
	public void remove(int threadId);
	
}
