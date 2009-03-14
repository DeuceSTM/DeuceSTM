package org.deuce.transaction.tl2.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.Exclude;

@Exclude
public class ShortWriteFieldAccess extends WriteFieldAccess {

	private final short value;

	public ShortWriteFieldAccess(short value, Object reference, long field) {
		super(reference, field);
		this.value = value;
	}

	@Override
	public void put() {
		UnsafeHolder.getUnsafe().putShort(reference, field, value);
	}

	public short getValue() {
		return value;
	}

}
