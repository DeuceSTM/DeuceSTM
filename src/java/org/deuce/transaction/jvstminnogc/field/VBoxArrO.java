package org.deuce.transaction.jvstminnogc.field;


import org.deuce.reflection.AddressUtil;
import org.deuce.reflection.UnsafeHolder;
import org.deuce.transaction.jvstminnogc.LockTable;
import org.deuce.transform.ExcludeInternal;
import org.deuce.transform.inplacemetadata.type.TxArrObjectField;

@ExcludeInternal
public class VBoxArrO extends TxArrObjectField implements VBoxO {
	public VersionO version;

	public VBoxArrO(Object[] arr, int idx) {
                super(arr, idx);
		version = new VersionO(0, read(), null);
	}
	
	public VBoxArrO(Object[] arr, int idx, Object dummy) {
          super(arr, idx, dummy);
          version = new VersionO(0, read(), null);
	}

	public boolean validate(Version version, int owner) {
		VersionO tmp = this.version;
		int l = lock;
		if ((l & LockTable.LOCK) != 0) {
			if ((l & LockTable.UNLOCK) != owner) {
				throw LockTable.LOCKED_VERSION_EXCEPTION;
			}
		}
		return tmp == version;
	}

	@Override
	public void commit(Object newValue, int txNumber) {
		VersionO ver = new VersionO(txNumber, newValue, this.version);
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
			__LOCK_FIELD__ = AddressUtil.getAddress(VBoxArrO.class.getDeclaredField("lock"));
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
