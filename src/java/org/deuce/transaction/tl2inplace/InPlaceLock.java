package org.deuce.transaction.tl2inplace;

/**
 * TL2 in-place metadata.
 * 
 * @author Ricardo Dias, Tiago Vale <{ricardo.dias,t.vale}@campus.fct.unl.pt}>
 */
public interface InPlaceLock {

//	boolean lock();
	boolean lock(Context locker);

	int checkLock(int clock);
	int checkLock(int clock, Context lockChecker);

	void checkLock2(int clock);

	void checkLock(int clock, int expected);

	void unLock();

	void setAndReleaseLock(int newClock);

}
