package org.deuce.transaction.tl2.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.Exclude;

@Exclude
public class BooleanWriteFieldAccess extends WriteFieldAccess {

	private final boolean value;

	public BooleanWriteFieldAccess(boolean value, Object reference, long field) {
		super(reference, field);
		this.value = value;
	}

	@Override
	public void put() {
		UnsafeHolder.getUnsafe().putBooleanVolatile(reference, field, getValue());
	}

	public boolean getValue() {
		return value;
	}
}
