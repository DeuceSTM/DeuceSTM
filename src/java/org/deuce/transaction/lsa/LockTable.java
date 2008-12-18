package org.deuce.transaction.lsa;

import java.util.concurrent.atomic.AtomicIntegerArray;

import org.deuce.transaction.TransactionException;
import org.deuce.transform.Exclude;

/**
 * @author Pascal Felber
 */
@Exclude
public class LockTable {

	// Failure transaction 
	final private static TransactionException FAILURE_EXCEPTION =
		new TransactionException("Fail on lock (already locked).");
	
	final private static int ARRAYSIZE = 1 << 20; // 2^20
	final private static int MASK = ARRAYSIZE - 1;
	final private static int LOCK = 1 << 31;
	final private static int IDMASK = LOCK - 1;

	// Array 32-bit lock words
	final private static AtomicIntegerArray locks = new AtomicIntegerArray(ARRAYSIZE);

	public static int lock(int hash, int id) throws TransactionException {
		assert hash <= MASK;
		while (true) {
			int lock = locks.get(hash);
			if ((lock & LOCK) != 0) {
				if ((lock & IDMASK) != id) {
					// Already locked by other thread
					throw FAILURE_EXCEPTION;
				} else {
					// We already own this lock
					return -1;
				}
			}

			if (locks.compareAndSet(hash, lock, id | LOCK)) {
				// Return old timestamp (lock bit is not set)
				return lock;
			}
		}
	}

	public static int checkLock(int hash, int id) throws TransactionException {
		assert hash <= MASK;
		int lock = locks.get(hash);
		if ((lock & LOCK) != 0) {
			if ((lock & IDMASK) != id) {
				// Already locked by other thread
				throw FAILURE_EXCEPTION;
			} else {
				// We already own this lock
				return -1;
			}
		}

		// Return old timestamp (lock bit is not set)
		return lock;
	}

	public static void setAndReleaseLock(int hash, int lock) {
		assert hash <= MASK;
		locks.set(hash, lock);
	}

	public static int hash(Object obj, long field) {
		int hash = System.identityHashCode(obj) + (int) field;
		return hash & MASK;
	}
}
