package org.deuce.transaction.tl2cm;

import java.util.concurrent.atomic.AtomicLongArray;

import org.deuce.transform.Exclude;

/**
 * Table of locks for accessed locations. Each lock is comprised of a 64-bit number that encodes
 * a version counter, the thread Id of the lock owner and the local clock value of the lock owner. * 
 * The lock is unlocked if and only if the owner field equals </code>NO_OWNER</code>.
 * 
 * <p>Based on Guy Korland's and Pascal Felber's work on <code>org.deuce.transaction.tl2.*</code> 
 * and <code>org.deuce.transaction.lsa*</code></p>
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 */
@Exclude
public class LockTable {

	public static final int OWNERSIZE = 8;	// Allowing for 2^OWNERSIZE threads in the system
	public static final int OWNERCLOCK_SIZE = 31 - OWNERSIZE;
	private static final int NO_OWNER = 0;
	private static final int SIZE = 1 << 20;
	private static final int HASH_MASK = 0xFFFFF;
	private static AtomicLongArray locks;
	
	static {
		long[] array = new long[SIZE];
		long initialLock = generateLock(NO_OWNER, 0, 0);
		for (int i=0; i<SIZE; i++) {
			array[i] = initialLock;
		}
		locks = new AtomicLongArray(array);
	}
	
	public static final long getLock(int hash) {
		int lockIndex = hash & HASH_MASK;
		return locks.get(lockIndex);
	}
	
	/**
	 * Locks the location specified by the hash code
	 * @param hash hash code of the location to lock
	 * @param owner thread Id of acquiring thread
	 * @param ownerClock local clock value of the acquiring thread
	 * @return array of 3 elements. The first element is a status indicator:</br>
	 * <li> 0 if the lock was acquired</li>
	 * <li> 1 if the lock wasn't acquired and the owner is known</li>
	 * <li> 2 if the lock wasn't acquired and the owner is unknown</li>
	 * The second element is the original lock value (if available) and the 
	 * third element if the new lock value (if available).
	 */
	public static final long[] lock(int hash, int owner, int ownerClock) {
		// read the lock
		int lockIndex = hash & HASH_MASK;
		long lock = locks.get(lockIndex);

		// prepare the result object
		long[] res = new long[3];
		res[1] = lock;
		
		// try to acquire the lock if it is free 
		if (!isLocked(lock)) {
			long newLock = generateLock(owner, ownerClock, getVersion(lock));
			boolean cas = locks.compareAndSet(lockIndex, lock, newLock);
			if (cas) {
				// 0 means lock acquired
				res[0] = 0L;
				res[2] = newLock;
			}
			else {
				// 2 means the lock wasn't locked when we 
				// entered the method but someone stole it before us
				res[0] = 2L; 
			}
		}
		else {
			// 1 means the lock could not be acquired
			if (getOwner(lock) == owner) {
				res[0] = 0;
				res[2] = lock;
			}
			else {
				res[0] = 1L;
			}
		}
		return res;
	}
	
	/**
	 * Attempts to lock the lock even though it might be owned by some other thread
	 * @param hash hash code of the location to lock
	 * @param owner thread Id of acquiring thread
	 * @param ownerClock local clock value of the acquiring thread
	 * @param version version of new lock value 
	 * @param expectedLock the expected lock value
	 * @return true if lock acquired, false otherwise
	 */
	public static final boolean forceLock(int hash, int owner, int ownerClock, int version, long expectedLock) {
		long newLock = generateLock(owner, ownerClock, version);
		int lockIndex = hash & HASH_MASK;
		return locks.compareAndSet(lockIndex, expectedLock, newLock);
	}
	
	/**
	 * Unlocks the lock if still owned by calling thread
	 * @param hash hash code of the location to lock
	 * @param threadId thread Id of the calling thread
	 */
	public static final void unLock(int hash, int threadId) {
		int lockIndex = hash & HASH_MASK;
		long lock = locks.get(lockIndex);
		// If I'm still the owner try to release the lock
		if (getOwner(lock) == threadId) {
			long newLock = generateLock(NO_OWNER, 0, getVersion(lock));
			// doesn't matter if the CAS succeeds or not. If it does,
			// the the lock is released. If not, then the lock was 
			// already taken by force by another thread
			locks.compareAndSet(lockIndex, lock, newLock);
		}
	}
	
	/**
	 * Updates the locks version and unlock it
	 * @param hash hash code of the location to lock
	 * @param updatedVersion new version to put on lock
	 */
	public static final void updateAndUnlock(int hash, int updatedVersion) {
		long newLock = generateLock(NO_OWNER, 0, updatedVersion);
		int lockIndex = hash & HASH_MASK;
		locks.set(lockIndex, newLock);
	}
	
	public static final boolean isLocked(long lock) {
		return getOwner(lock) != NO_OWNER;
	}
	
	public static final int getVersion(long lock) {
		return (int) lock;
	}
	
	public static final int getOwner(long lock) {
		int ownerSection = (int)(lock >> 32);
		return ownerSection >> OWNERCLOCK_SIZE;
	}
	
	public static final int getOwnerClock(long lock) {
		int ownerSection = (int)(lock >> 32);
		return ownerSection & ((1<<OWNERCLOCK_SIZE)-1);
	}
	
	public static final long generateLock(int owner, int ownerClock, int version) {
		int ownerSection = (owner << OWNERCLOCK_SIZE) | ownerClock;
		long lock = (((long)ownerSection) << 32) | version;
		return lock;
	}
	
	public static final int hash(Object obj, long field) {
		int hash = System.identityHashCode(obj) + (int) field;
		return hash & HASH_MASK;
	}
	
}

