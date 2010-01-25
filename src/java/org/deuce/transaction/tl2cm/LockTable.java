package org.deuce.transaction.tl2cm;

import java.util.concurrent.atomic.AtomicLongArray;

import org.deuce.transform.Exclude;

/**
 * Table of locks for accessed locations. Each lock is comprised of a 64-bit number that encodes
 * a version counter and the thread Id of the lock owner. The lock is unlocked if and only if the 
 * owner equals </code>NO_OWNER</code>.
 * 
 * <p>The layout of the lock variable is: <code>|  owner (32 bits)  |  version (32 bits)  |</code></p>
 * 
 * <p>Based on Guy Korland's and Pascal Felber's work on <code>org.deuce.transaction.tl2.*</code> 
 * and <code>org.deuce.transaction.lsa*</code></p>
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 */
@Exclude
public class LockTable {

	private static final int NO_OWNER = 0;
	private static final int SIZE = 1 << 20;
	private static final int MASK = SIZE - 1;
	private static AtomicLongArray locks;
	
	static {
		long[] array = new long[SIZE];
		long initialLock = generateLock(0, NO_OWNER);
		for (int i=0; i<SIZE; i++) {
			array[i] = initialLock;
		}
		locks = new AtomicLongArray(array);
	}
	
	public static long getLock(int hash) {
		return locks.get(hash);
	}
	
	/**
	 * Locks the location specified by the hash code
	 * @param hash hash code of the location to lock
	 * @param threadId Id of thread 
	 * @return threadId if the lock is acquired, -1 if the lock is not acquired but the lock owner
	 * could not be obtained or the thread Id of the lock owner otherwise.
	 */
	//TODO: need to return the lock variable here. The new one if succeeded and the old one if not
	// -1 if I don't know who's the owner
	public static long lock(int hash, int threadId, int version) {
		long lock = locks.get(hash);
		// try to acquire the lock if it is free 
		if (!isLocked(lock)) {
			long newLock = generateLock(threadId, version);
			boolean cas = locks.compareAndSet(hash, lock, newLock);
			if (cas) {
				return newLock;
			}
			else {
				// to indicate that the lock wasn't locked when we 
				// entered the method but someone stole it before us
				return -1; 
			}
		}
		else {
			return lock;
		}
	}
	
	public static boolean forceLock(int hash, long expectedLock, int threadId, int version) {
		long newLock = generateLock(threadId, version);
		return locks.compareAndSet(hash, expectedLock, newLock);
	}

	/**
	 * Unlocks the location specified by the hash code.
	 * @param hash hash code of location to unlock
	 * @param threadId Id of thread 
	 */
	public static void unLock(int hash, int threadId, int version) {
		long lock = locks.get(hash);
		// If I'm still the owner try to release the lock
		if (getOwner(lock) == threadId) {
			// If the version is -1 it indicates that we should
			// use the version of the lock
			if (version == -1) {
				version = getVersion(lock);
			}
			long newLock = generateLock(NO_OWNER, version);
			// doesn't matter if the CAS succeeds or not. If it does,
			// the the lock is released. If not, then the lock was 
			// already taken by force by another thread
			locks.compareAndSet(hash, lock, newLock);
		}
		////logger.log(Level.INFO, "unlocking location {0} - new value is {1}", new Object[]{hash, lockToStr(newLock)});
	}

	
	/**
	 * Checks that the location the transaction just read is still valid, i.e
	 * it's version has not changed from the one observed in <code>checkBeforeRead</code> 
	 * and that the location is not locked.
	 *
	 * @param hash hash 
	 * @param observedLockVersion the lock version observed by the transaction in <code>checkBeforeRead</code>
	 * @return -2 if the location is locked, -1 if the location's version is 
	 * not as expected, 0 otherwise
	 */
	public static int checkAfterRead(int hash, long observedLockVersion) {
		long lock = locks.get(hash);
		if (isLocked(lock)) {
			return -2;
		}
		else if (observedLockVersion != getVersion(lock)) {
			return -1;
		}
		else {
			return 0;
		}
	}

	public static void updateAndUnlock(int hash, int updatedVersion) {
		long newLock = generateLock(NO_OWNER, updatedVersion);
		locks.set(hash, newLock);
		////logger.log(Level.INFO, "setAndReleaseLock() end | hash={0} TID={1} lock=[{2}]", new Object[]{hash, threadId, lockToStr(newLock)});
	}
	
	public static final boolean isLocked(long lock) {
		return getOwner(lock) != NO_OWNER;
	}
	
	public static final int getVersion(long lock) {
		return (int) lock;
	}
	
	public static final int getOwner(long lock) {
		return (int) (lock >> 32);
	}
	
	public static final long generateLock(int owner, int version) {
		long lock = (((long)owner) << 32) | version;
		return lock;
	}
	
	public static int hash(Object obj, long field) {
		int hash = System.identityHashCode(obj) + (int) field;
		return hash & MASK;
	}
	
//	public static String lockToStr(long lock) {
//		if (logger.isLoggable(Level.INFO)) {
//			StringBuilder sb = new StringBuilder("locked=");
//			sb.append(isLocked(lock));
//			sb.append(" version=");
//			sb.append(getVersion(lock));
//			sb.append(" owner=");
//			sb.append(getOwner(lock));
//			String str = sb.toString();
//			return str;
//		}
//		return "insufficient log level";
//	}
	
}

