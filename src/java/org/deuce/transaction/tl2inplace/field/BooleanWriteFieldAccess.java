package org.deuce.transaction.tl2inplace.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.ExcludeInternal;
import org.deuce.transform.inplacemetadata.type.TxField;

@ExcludeInternal
public class BooleanWriteFieldAccess extends WriteFieldAccess {

	private boolean value;

	public void set(boolean value, TxField field) {
		super.init(field);
		this.value = value;
	}

	@Override
	public void put() {
		UnsafeHolder.getUnsafe().putBoolean(field.ref, field.address, getValue());
	}

	public boolean getValue() {
		return value;
	}
}
