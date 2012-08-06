package org.deuce.transaction.tl2inplace;

public interface InPlaceLock {

	boolean lock();

	int checkLock(int clock);

	void checkLock2(int clock);

	void checkLock(int clock, int expected);

	void unLock();

	void setAndReleaseLock(int newClock);

}
