package org.deuce.transaction.norec.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.ExcludeInternal;

/**
 * @author Pascal Felber
 */
@ExcludeInternal
public class DoubleFieldAccess extends FieldAccess {

	final private double value;

	public DoubleFieldAccess(Object reference, long field, double value) {
		super(reference, field);
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	@Override
	public void writeField() {
		UnsafeHolder.getUnsafe().putDouble(reference, field, value);
	}

	@Override
	public boolean validate() {
		return value == UnsafeHolder.getUnsafe().getDouble(reference, field);
	}
}