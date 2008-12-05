package org.deuce.transaction.tl2;

import java.util.concurrent.atomic.AtomicIntegerArray;

import org.deuce.transaction.TransactionException;
import org.deuce.transform.Exclude;

@Exclude
public class LockTable {

	// Failure transaction 
	final private static TransactionException FAILURE_EXCEPTION = new TransactionException( "Faild on lock.");
	
	final private static int MASK = 0xFFFFF;
	final private static int LOCK = 1 << 31;
	final private static int UNLOCK = ~LOCK;

	final private static AtomicIntegerArray locks =  new AtomicIntegerArray(1<<20); // array of 2^20 entries of 32-bit lock words

	public static void lock( int hash) throws TransactionException{
		int lockIndex = hash & MASK;
		int lock = locks.get(lockIndex); 
		if( (lock & LOCK) != 0){ // FIXME check for self locking
			throw FAILURE_EXCEPTION; // TODO unlock all or spin lock
		}

		boolean isLocked = locks.compareAndSet(lockIndex, lock, lock | LOCK);

		if( !isLocked)
			throw FAILURE_EXCEPTION;
	}

	public static void checkLock(int hash, int clock) {
		int lockIndex = hash & MASK;
		int lock = locks.get(lockIndex);

		if( clock < (lock & UNLOCK)) // check the clock without lock, TODO check if this is the best way
			throw FAILURE_EXCEPTION;
	}

	public static void unLock( int hash){
		int lockIndex = hash & MASK;
		int lockedValue = locks.get( lockIndex);
		int unlockedValue = lockedValue & UNLOCK;
		locks.set(lockIndex, unlockedValue);
	}

	public static void setAndReleaseLock( int hash, int newClock){
		int lockIndex = hash & MASK;
		locks.set(lockIndex, newClock);
	}
}
