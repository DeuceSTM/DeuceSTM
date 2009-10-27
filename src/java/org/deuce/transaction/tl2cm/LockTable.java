package org.deuce.transaction.tl2cm;

import java.util.concurrent.atomic.AtomicLongArray;

import org.deuce.transaction.TransactionException;
import org.deuce.transform.Exclude;

/**
 * Table of locks for accessed locations. Each lock is comprised of a 64-bits number that encodes
 * a locked bit, a version counter and the thread Id of the lock owner.
 * 
 * Based on Guy Korland's and Pascal Felber's work on <code>org.deuce.transaction.tl2.*</code> 
 * and <code>org.deuce.transaction.lsa*</code>
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 */
@Exclude
public class LockTable {

	public static final int INITIAL_OWNER = 0;
	//private static final Logger logger = logger.getLogger(Context.TL2CM_LOGGER);
	private static final int SIZE = 1 << 20;
	private static final long LOCK_BIT = 1L << 63;
	private static final int MASK = SIZE - 1;
	private static final long VER_MASK = (1L << 49) - 1L;
	private static final long ID_MASK = ((1L << 14) - 1L) << 49;
	private static final TransactionException FAILURE_EXCEPTION = new TransactionException( "Faild on lock.");
	
	private static AtomicLongArray locks;
	static {
		long[] array = new long[SIZE];
		long initialLock = generateLock(false, 0, INITIAL_OWNER);
		for (int i=0; i<SIZE; i++) {
			array[i] = initialLock;
		}
		locks = new AtomicLongArray(array);
	}
	
	/**
	 * Locks the location specified by the hash code
	 * @param hash hash code of the location to lock
	 * @param threadId Id of thread 
	 * @return threadId if the lock is acquired, -1 if the lock is not acquired but the lock owner
	 * could not be obtained or the thread Id of the lock owner otherwise.
	 */
	public static int lock(int hash, int threadId) {
		assert hash <= MASK;
		long lock = locks.get(hash);
		// try to acquire the lock if it is free 
		if (!isLocked(lock)) {
			long version = getVersion(lock);
			long newLock = generateLock(true, version, threadId);
			////logger.log(Level.INFO, "lock() | before CAS | TID={0} hash={1} lock=[{2}] newLock=[{3}]", new Object[]{threadId, hash, lockToStr(lock), lockToStr(newLock)});
			boolean cas = locks.compareAndSet(hash, lock, newLock);
			////logger.log(Level.INFO, "lock() | after CAS | TID={0} hash={1} cas={2} lock={3}", new Object[]{threadId, hash, cas, lockToStr(locks.get(hash))});
			if (cas) {
				return threadId;
			}
			else {
				// to indicate that the lock wasn't locked when we 
				// entered the method but someone stole it before us
				return -1; 
			}
		}
		// lock is already acquired, return its owner
		else {
			int owner = getOwner(lock);
			////logger.log(Level.INFO, "lock() | returning {0}", new Object[]{owner});
			return owner;
		}
	}
	
	/**
	 * Unlocks the location specified by the hash code
	 * @param hash hash code of location to unlock
	 */
	public static void unLock(int hash) {
		assert hash <= MASK;
		long lock = locks.get(hash);
		long newLock = generateLock(false, getVersion(lock), INITIAL_OWNER);
		locks.set(hash, newLock);
		////logger.log(Level.INFO, "unlocking location {0} - new value is {1}", new Object[]{hash, lockToStr(newLock)});
	}

	/**
	 * 
	 * @param hash
	 * @param expected
	 * @return
	 */
	public static long checkAndGetLock(int hash, int expected) {
		assert hash <= MASK;
		long lock = locks.get(hash);
		long actualVersion = getVersion(lock);
		if (expected < actualVersion) {
			//logger.log(Level.INFO, "checkAndGetLock; Cannot validate; hash={0} lock={1} expectedVersion={2} actualVersion={3}", new Object[]{hash, lock, expected, actualVersion});
			throw FAILURE_EXCEPTION;
		}
		else {
			return lock;
		}
	}

	/**
	 * Checks that the lock is unlocked and that 
	 * @param hash
	 * @param version
	 * @param expectedLock
	 */
	public static void checkLock(int hash, int version, long expectedLock) {
		assert hash <= MASK;
		long lock = checkAndGetLock(hash, version);
		if (lock != expectedLock || isLocked(lock)) {
			throw FAILURE_EXCEPTION;
		}
	}

	public static void setAndReleaseLock(int hash, long updatedVersion, int threadId) {
		assert hash <= MASK;
		long newLock = generateLock(false, updatedVersion, INITIAL_OWNER);
		locks.set(hash, newLock);
		////logger.log(Level.INFO, "setAndReleaseLock() end | hash={0} TID={1} lock=[{2}]", new Object[]{hash, threadId, lockToStr(newLock)});
	}
	
	public static final boolean isLocked(long lock) {
		return (lock & LOCK_BIT) != 0;
	}
	
	public static final long getVersion(long lock) {
		return (lock & VER_MASK);
	}
	
	public static final int getOwner(long lock) {
		return (int)((lock & ID_MASK) >> 49);
	}
	
	public static final long generateLock(boolean locked, long version, long owner) {
		long lock = locked ? LOCK_BIT : 0;
		lock = lock | (owner << 49);
		lock = lock | version;
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

