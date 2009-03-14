package org.deuce.transaction.tl2.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.Exclude;

@Exclude
public class FloatWriteFieldAccess extends WriteFieldAccess {

	private final float value;

	public FloatWriteFieldAccess(float value, Object reference, long field) {
		super(reference, field);
		this.value = value;
	}

	@Override
	public void put() {
		UnsafeHolder.getUnsafe().putFloat(reference, field, value);
	}

	public float getValue() {
		return value;
	}

}
