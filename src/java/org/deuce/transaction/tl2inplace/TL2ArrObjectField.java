package org.deuce.transaction.tl2inplace;

import org.deuce.reflection.AddressUtil;
import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.ExcludeInternal;
import org.deuce.transform.inplacemetadata.type.TxArrObjectField;

@ExcludeInternal
public class TL2ArrObjectField extends TxArrObjectField implements InPlaceLock {

	public TL2ArrObjectField(Object[] arr, int idx) {
		super(arr, idx);
		// TODO Auto-generated constructor stub
	}

	public TL2ArrObjectField(Object[] arr, int idx, Object dummy) {
		super(arr, idx, dummy);
		// TODO Auto-generated constructor stub
	}

	private static long __LOCK_FIELD__;
	static {
		try {
			__LOCK_FIELD__ = AddressUtil.getAddress(TL2ArrObjectField.class.getDeclaredField("lock"));
		} catch (SecurityException e) {
		} catch (NoSuchFieldException e) {
		}
	}
	public volatile int lock = 0;

	public int checkLock(int clock) {
		int l = lock;
		if (clock < (lock & LockTable.UNLOCK)) {// check the clock without lock,
												// TODO check if this is the
												// best way
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

	public boolean lock() {
		int l = lock;

		if ((l & LockTable.LOCK) != 0) {
			throw LockTable.FAILURE_EXCEPTION;
		}

		boolean isLocked = UnsafeHolder.getUnsafe().compareAndSwapInt(this, __LOCK_FIELD__, l, l | LockTable.LOCK);

		if (!isLocked) {
			throw LockTable.FAILURE_EXCEPTION;
		}

		return true;
	}

	public void setAndReleaseLock(int newClock) {
		lock = newClock;
	}

	public void unLock() {
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
