package org.deuce.transaction.tl2cm.contexts;

import java.util.concurrent.CopyOnWriteArrayList;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transform.Exclude;

/**
* @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
* @since 1.2
*/
@Exclude
public class COWALContextsMap implements ContextsMap {

	private final CopyOnWriteArrayList<Context> list;
	
	public COWALContextsMap() {
		this(256);
	}
	
	public COWALContextsMap(int capacity) {
		list = new CopyOnWriteArrayList<Context>(new Context[capacity]);
	}
	
	public final Context get(int threadId) {
		return list.get(threadId);
	}

	public final void put(int threadId, Context context) {
		list.set(threadId, context);
	}

	public final void remove(int threadId) {
		list.set(threadId, null);
	}

}
