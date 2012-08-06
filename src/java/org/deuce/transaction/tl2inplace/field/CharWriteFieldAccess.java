package org.deuce.transaction.tl2inplace.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.ExcludeInternal;
import org.deuce.transform.inplacemetadata.type.TxField;

@ExcludeInternal
public class CharWriteFieldAccess extends WriteFieldAccess {

	private char value;

	public void set(char value, TxField field) {
		super.init(field);
		this.value = value;
	}

	@Override
	public void put() {
		UnsafeHolder.getUnsafe().putChar(field.ref, field.address, value);
	}

	public char getValue() {
		return value;
	}

}
