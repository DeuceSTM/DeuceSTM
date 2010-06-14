package org.deuce.transaction.norec.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.Exclude;

/**
 * @author Pascal Felber
 */
@Exclude
public class BooleanFieldAccess extends FieldAccess {

	private boolean value;

	public BooleanFieldAccess(Object reference, long field, boolean value) {
		super(reference, field);
		this.value = value;
	}

	public boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	@Override
	public void writeField() {
		UnsafeHolder.getUnsafe().putBoolean(reference, field, value);
	}

	@Override
	public boolean validate() {
		return value == UnsafeHolder.getUnsafe().getBoolean(reference, field);
	}
}