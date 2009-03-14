package org.deuce.transaction.tl2.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.Exclude;

@Exclude
public class IntWriteFieldAccess extends WriteFieldAccess {

	private final int value;

	public IntWriteFieldAccess(int value, Object reference, long field) {
		super(reference, field);
		this.value = value;
	}

	@Override
	public void put() {
		UnsafeHolder.getUnsafe().putInt(reference, field, value);
	}

	public int getValue() {
		return value;
	}

}
