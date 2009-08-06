package org.deuce.transaction.lsacm;

import java.util.concurrent.atomic.AtomicLongArray;

import org.deuce.transaction.TransactionException;
import org.deuce.transaction.lsacm.Context;
import org.deuce.transaction.lsacm.ContentionManager.ConflictType;
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
	// Lock bits
	final private static long WLOCK = 1L << 63;
	final private static long RLOCK = 1L << 62;
	final private static long RWLOCK = (WLOCK | RLOCK);
	// Timestamp
	final private static int TSBITS = 48; // 48 bits
	final private static long TSMASK = (1L << TSBITS) - 1L;
	// Lock owner
	final private static int IDBITS = 14; // 14 bits
	final private static int IDOFFSET = TSBITS;
	final private static long IDMASK = ((1L << IDBITS) - 1L) << IDOFFSET;

	// Lock modes
	final public static long LOCKED_WRITE = -1L;
	final public static long LOCKED_READ = -2L;

	// Array 32-bit lock words
	final private static AtomicLongArray locks = new AtomicLongArray(ARRAYSIZE);

	public static long lock(Context context, int hash, int id, boolean write) throws TransactionException {
		assert hash <= MASK;
		while (true) {
			long lock = locks.get(hash);
			if ((lock & RWLOCK) != 0) {
				int owner = (int)((lock & IDMASK) >> IDOFFSET);
				if (owner != id) {
					// Already locked by other thread
					ConflictType type;
					if ((lock & WLOCK) != 0)
						type = (write ? ConflictType.WW : ConflictType.WR);
					else
						type = (write ? ConflictType.RW : ConflictType.RR);
					if (context.conflict(owner, type, hash, lock)) {
						// We win: retry
						continue;
					}
					throw FAILURE_EXCEPTION;
				} else {
					// We already own this lock
					if (write && (lock & WLOCK) == 0) {
						// Upgrade lock (use CAS as another thread might be releasing our locks)
						if (!locks.compareAndSet(hash, lock, lock | WLOCK))
							throw FAILURE_EXCEPTION;
					}
					return ((lock & WLOCK) != 0 ? LOCKED_WRITE : LOCKED_READ);
				}
			}

			long l = lock | ((long)id << IDOFFSET) | (write ? WLOCK : RLOCK);
			if (locks.compareAndSet(hash, lock, l)) {
				// Return old timestamp (lock bit is not set)
				return lock & TSMASK;
			}
		}
	}

	public static long checkLock(Context context, int hash, int id) throws TransactionException {
		assert hash <= MASK;
		while (true) {
			long lock = locks.get(hash);
			if ((lock & WLOCK) != 0) {
				int owner = (int)((lock & IDMASK) >> IDOFFSET);
				if (owner != id) {
					// Already locked by other thread
					throw FAILURE_EXCEPTION;
				} else {
					// We already own this lock
					return ((lock & WLOCK) != 0 ? LOCKED_WRITE : LOCKED_READ);
				}
			}
			// Return old timestamp (write lock bit is not set)
			return lock & TSMASK;
		}
	}

	public static long readLock(int hash) {
		assert hash <= MASK;
		return locks.get(hash);
	}

	public static void setAndReleaseLock(int hash, long lock) {
		assert hash <= MASK;
		locks.set(hash, lock);
	}

	public static int hash(Object obj, long field) {
		int hash = System.identityHashCode(obj) + (int) field;
		return hash & MASK;
	}
}
