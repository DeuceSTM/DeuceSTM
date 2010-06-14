package org.deuce.transaction.norec.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.Exclude;

/**
 * @author Pascal Felber
 */
@Exclude
public class FloatFieldAccess extends FieldAccess {

	private float value;

	public FloatFieldAccess(Object reference, long field, float value) {
		super(reference, field);
		this.value = value;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	@Override
	public void writeField() {
		UnsafeHolder.getUnsafe().putFloat(reference, field, value);
	}

	@Override
	public boolean validate() {
		return value == UnsafeHolder.getUnsafe().getFloat(reference, field);
	}
}