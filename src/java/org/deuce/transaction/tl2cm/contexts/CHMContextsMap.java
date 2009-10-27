package org.deuce.transaction.tl2cm.contexts;

import java.util.concurrent.ConcurrentHashMap;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transform.Exclude;

/**
* @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
* @since 1.2
*/
@Exclude
public class CHMContextsMap implements ContextsMap {

	private final ConcurrentHashMap<Integer, Context> map = new ConcurrentHashMap<Integer, Context>();
	
	public Context get(int threadId) {
		return map.get(threadId);
	}

	public final void put(int threadId, Context context) {
		map.put(threadId, context);
	}

	public final void remove(int threadId) {
		map.remove(threadId);
	}

}
