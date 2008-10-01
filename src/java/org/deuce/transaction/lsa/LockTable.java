package org.deuce.transaction.lsa;

import java.util.concurrent.atomic.AtomicIntegerArray;

import org.deuce.transaction.TransactionException;
import org.deuce.transform.Exclude;

/**
 * @author Pascal Felber
 * @since 0.1
 */
@Exclude
public class LockTable {

	final private static int ARRAYSIZE = 1 << 20; // 2^20
	final private static int MASK = ARRAYSIZE - 1;
	final private static int LOCK = 1 << 31;
	final private static int IDMASK = LOCK - 1;

	final private static AtomicIntegerArray locks = new AtomicIntegerArray(ARRAYSIZE); // array 32-bit lock words

	public static int lock(int hash, int id) throws TransactionException {
		assert hash <= MASK;
		while (true) {
			int lock = locks.get(hash);
			if ((lock & LOCK) != 0) {
				if ((lock & IDMASK) != id) {
					// Already locked by other thread
					throw new TransactionException("Fail on acquire lock (already locked).");
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
				throw new TransactionException("Fail on check lock (already locked).");
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
