package org.deuce.transaction.mvstm.field;

import org.deuce.reflection.AddressUtil;
import org.deuce.reflection.UnsafeHolder;
import org.deuce.transaction.mvstm.LockTable;
import org.deuce.transform.ExcludeInternal;
import org.deuce.transform.inplacemetadata.type.TxArrCharField;

/**
 * 
 * @author Ricardo Dias <ricardo.dias@campus.fct.unl.pt>
 */
@ExcludeInternal
public class VBoxArrC extends TxArrCharField implements VBoxC {
	public VersionC version;

	public VBoxArrC(char[] arr, int idx) {
	        super(arr, idx);
		version = new VersionC(0, read(), null);
	}

	public boolean validate(Version version, int owner) {
		Version tmp = this.version;
		int l = lock;
		if ((l & LockTable.LOCK) != 0) {
			if ((l & LockTable.UNLOCK) != owner) {
				throw LockTable.LOCKED_VERSION_EXCEPTION;
			}
		}
		return tmp == version;
	}

	@Override
	public void commit(char newValue, int txNumber) {
		VersionC ver = new VersionC(txNumber, newValue, version);
		this.version = ver;
	}

	@Override
	public Version get(int version) {
		if ((lock & LockTable.LOCK) != 0) {
			throw LockTable.LOCKED_VERSION_EXCEPTION;
		}
		return this.version.get(version);
	}

	private static long __LOCK_FIELD__;
	static {
		try {
			__LOCK_FIELD__ = AddressUtil.getAddress(VBoxArrC.class.getDeclaredField("lock"));
		} catch (SecurityException e) {
		} catch (NoSuchFieldException e) {
		}
	}
	public volatile int lock = 0;
	
	@Override
	public boolean lock(int owner) {
		int l = lock;
		if ((l & LockTable.LOCK) != 0) {
			throw LockTable.LOCKED_VERSION_EXCEPTION;
		}
		if (!UnsafeHolder.getUnsafe().compareAndSwapInt(this, __LOCK_FIELD__, l, l | owner | LockTable.LOCK)) {
			throw LockTable.LOCKED_VERSION_EXCEPTION;
		}
		return true;
	}

	@Override
	public void unLock() {
		lock = 0;
	}

}
