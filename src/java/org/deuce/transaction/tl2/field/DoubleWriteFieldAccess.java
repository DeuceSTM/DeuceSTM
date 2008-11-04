package org.deuce.transaction.tl2.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.Exclude;

@Exclude
public class DoubleWriteFieldAccess extends WriteFieldAccess {

	private final double value;

	public DoubleWriteFieldAccess(double value, Object reference, long field) {
		super(reference, field);
		this.value = value;
	}

	@Override
	public void put() {
		UnsafeHolder.getUnsafe().putDoubleVolatile(reference, field, value);
	}

	public double getValue() {
		return value;
	}
}
