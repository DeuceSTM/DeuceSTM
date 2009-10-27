package org.deuce.transaction.tl2cm.contexts;

import java.util.HashMap;
import java.util.Map;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transform.Exclude;

/**
* @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
* @since 1.2
*/
@Exclude
public class HashMapContextsMap implements ContextsMap {

	private final Map<Integer, Context> map = new HashMap<Integer, Context>();
	
	public final Context get(int threadId) {
		return map.get(threadId);
	}

	public final void put(int threadId, Context context) {
		map.put(threadId, context);
	}

	public final void remove(int threadId) {
		map.remove(threadId);
	}

}
