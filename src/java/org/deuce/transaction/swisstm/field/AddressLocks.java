package org.deuce.transaction.swisstm.field;

import java.util.concurrent.atomic.AtomicInteger;

import org.deuce.transform.Exclude;

@Exclude
public class AddressLocks {
	public static final int READ_LOCKED = -1;
	public static final int WRITE_UNLOCKED = -1;

	private final AtomicInteger rLock;
	private final AtomicInteger wLock;

	public AddressLocks() {
		this(0, 0);
	}

	public AddressLocks(int rLock, int wLock) {
		this.rLock = new AtomicInteger(rLock);
		this.wLock = new AtomicInteger(wLock);
	}

	public int getWLockThreadID() {
		if (isLocked(this.wLock)) {
			return this.wLock.get() >> 1;
		} else {
			return WRITE_UNLOCKED;
		}
	}

	public void lockWLock(int threadID) {
		this.wLock.set((threadID << 1) | 1);
	}

	public void unlockWLock() {
		this.wLock.set(0);
	}

	public int getRLockVersion() {
		if (isLocked(this.rLock)) {
			return READ_LOCKED;
		} else {
			return this.rLock.get() >> 1;
		}
	}

	public void lockRLock() {
		this.rLock.set(1);
	}

	public void unlockRLock(int version) {
		this.rLock.set(version << 1);
	}

	public boolean casWLock(int expect, int update) {
		if (expect == WRITE_UNLOCKED) {
			expect = 0;
		}
		return this.wLock.compareAndSet(expect, update);
	}

	private boolean isLocked(AtomicInteger lock) {
		return (lock.get() & 1) == 1;
	}
}
