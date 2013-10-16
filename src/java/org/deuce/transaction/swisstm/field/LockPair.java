package org.deuce.transaction.swisstm.field;

import java.util.concurrent.atomic.AtomicInteger;

import org.deuce.transform.Exclude;

/**
 * Contains a r-lock and a w-lock
 * r-lock can have a version number or the READ_LOCKED value
 * w-lock can have a transaction id or the WRITE_UNLOCKED value
 * 
 * The values are internally represented by an int. The least significant
 * bit tells if the lock is locked and the remaining bits contain the data
 * (version or transaction id)
 */
@Exclude
public final class LockPair {

	public static final int READ_LOCKED = -1;
	public static final int WRITE_UNLOCKED = -1;

	private final AtomicInteger rLock;
	private final AtomicInteger wLock;

	public LockPair() {
		this(0, 0);
	}

	private LockPair(int rLock, int wLock) {
		this.rLock = new AtomicInteger(rLock);
		this.wLock = new AtomicInteger(wLock);
	}

	public int getWLockTransactionID() {
		int lockValue = this.wLock.get();
		if (isLocked(lockValue)) {
			return lockValue >>> 1;
		} else {
			return WRITE_UNLOCKED;
		}
	}

	public void lockWLock(int transactionID) {
		this.wLock.set(getWLockInternalValue(transactionID));
	}

	public void unlockWLock() {
		this.wLock.set(0);
	}

	public int getRLockVersion() {
		int lockValue = this.rLock.get();
		if (isLocked(lockValue)) {
			return READ_LOCKED;
		} else {
			return lockValue >>> 1;
		}
	}

	public void lockRLock() {
		this.rLock.set(1);
	}

	public void unlockRLock(int version) {
		this.rLock.set(version << 1);
	}

	public boolean casWLock(int expectTransactionID, int newTransactionID) {
		expectTransactionID = getWLockInternalValue(expectTransactionID);
		newTransactionID =  getWLockInternalValue(newTransactionID);
		return this.wLock.compareAndSet(expectTransactionID, newTransactionID);
	}

	private boolean isLocked(int lockValue) {
		return (lockValue & 1) == 1;
	}

	private int getWLockInternalValue(int transactionID) {
		if (transactionID == WRITE_UNLOCKED) {
			return 0;
		} else {
			return (transactionID << 1) | 1;
		}
	}
}
