package org.deuce.transaction.tl2cm.contexts;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.deuce.transaction.tl2cm.Context;
import org.deuce.transform.Exclude;

/**
* @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
* @since 1.2
*/
@Exclude
public class RWLockContextsMap implements ContextsMap {

	private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
	private final Map<Integer, Context> map = new HashMap<Integer, Context>();
	
	public Context get(int threadId) {
		lock.readLock().lock();
		try {
			return map.get(threadId);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	public final void put(int threadId, Context context) {
		lock.writeLock().lock();
		try {
			map.put(threadId, context);
		}
		finally {
			lock.writeLock().unlock();
		}
	}

	public final void remove(int threadId) {
		lock.writeLock().lock();
		try {
			map.remove(threadId);
		}
		finally {
			lock.writeLock().unlock();
		}
	}

}
