package org.deuce.transaction.tl2inplace;

import org.deuce.reflection.AddressUtil;
import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.ExcludeInternal;
import org.deuce.transform.inplacemetadata.type.TxArrBoolField;

/**
 * TL2 in-place metadata.
 * 
 * @author Ricardo Dias, Tiago Vale <{ricardo.dias,t.vale}@campus.fct.unl.pt}>
 */
@ExcludeInternal
public class TL2ArrBoolField extends TxArrBoolField implements InPlaceLock {

	public TL2ArrBoolField(boolean[] arr, int idx) {
		super(arr, idx);
	}

	private static long __LOCK_FIELD__;
	static {
		try {
			__LOCK_FIELD__ = AddressUtil.getAddress(TL2ArrBoolField.class.getDeclaredField("lock"));
		} catch (SecurityException e) {
		} catch (NoSuchFieldException e) {
		}
	}
	public volatile int lock = 0;
	private volatile Context lockHolder; // Object reference

	public int checkLock(int clock) {
		int l = lock;
		if (clock < (l & LockTable.UNLOCK) || (l & LockTable.LOCK) != 0)
			throw LockTable.FAILURE_EXCEPTION;

		return l;
	}
	
	public int checkLock(int clock, Context lockChecker) {
		int l = lock;
		Context lh = lockHolder;
		
		if (clock < (l & LockTable.UNLOCK))
			throw LockTable.FAILURE_EXCEPTION;
		
		// already locked, and not by lockChecker
		if (((l & LockTable.LOCK) != 0) && (lh != lockChecker)) {
			throw LockTable.FAILURE_EXCEPTION;	
		}
		
		return l;
	}

	public void checkLock(int clock, int expected) {
		int l = lock;
		if (l != expected || clock < (l & LockTable.UNLOCK) || (l & LockTable.LOCK) != 0) {
			throw LockTable.FAILURE_EXCEPTION;
		}
	}

	public boolean lock(Context locker) {
		int l = lock;
		Context lh = lockHolder;

		if ((l & LockTable.LOCK) != 0) {
			if (lh == locker)
				return true;
			throw LockTable.FAILURE_EXCEPTION;
		}

		boolean isLocked = UnsafeHolder.getUnsafe().compareAndSwapInt(this, __LOCK_FIELD__, l, l | LockTable.LOCK);

		if (!isLocked) {
			throw LockTable.FAILURE_EXCEPTION;
		}
		
		lockHolder = locker; // mark as "locked by"

		return true;
	}

	public void setAndReleaseLock(int newClock) {
		lockHolder = null;
		lock = newClock;
	}

	public void unLock() {
		lockHolder = null;
		lock = lock & LockTable.UNLOCK;
	}

	@Override
	public void checkLock2(int clock) {
		int l = lock;
		if ((l & LockTable.LOCK) != 0 || clock < (l & LockTable.UNLOCK)) {
			throw LockTable.FAILURE_EXCEPTION;
		}

	}

}
