package org.deuce.transaction.tl2inplace.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.ExcludeInternal;
import org.deuce.transform.inplacemetadata.type.TxField;

@ExcludeInternal
public class ObjectWriteFieldAccess extends WriteFieldAccess {

	private Object value;

	public void set(Object value, TxField field) {
		super.init(field);
		this.value = value;
	}

	@Override
	public void put() {
		UnsafeHolder.getUnsafe().putObject(field.ref, field.address, value);
		value = null;
	}

	public Object getValue() {
		return value;
	}
}
