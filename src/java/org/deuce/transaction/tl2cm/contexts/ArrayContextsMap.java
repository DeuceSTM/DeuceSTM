package org.deuce.transaction.tl2cm.contexts;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transaction.tl2cm.Status;
import org.deuce.transform.Exclude;

/**
* @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
* @since 1.2
*/
@Exclude
public class ArrayContextsMap implements ContextsMap {

	private final Context[] arr = new Context[256];
	
	public Context get(int threadId) {
		Context context = arr[threadId];
		if (Status.INACTIVE != context.getStatus()) {
			return context;
		}
		else {
			return null;
		}
	}

	public final void put(int threadId, Context context) {
		arr[threadId] = context;
	}

	public final void remove(int threadId) {
		arr[threadId].setStatus(Status.INACTIVE);
	}

}
