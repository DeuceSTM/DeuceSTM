package org.deuce.transaction.tl2inplace.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.ExcludeInternal;
import org.deuce.transform.inplacemetadata.type.TxField;

@ExcludeInternal
public class ShortWriteFieldAccess extends WriteFieldAccess {

	private short value;

	public void set(short value, TxField field) {
		super.init(field);
		this.value = value;
	}

	@Override
	public void put() {
		UnsafeHolder.getUnsafe().putShort(field.ref, field.address, value);
	}

	public short getValue() {
		return value;
	}

}
