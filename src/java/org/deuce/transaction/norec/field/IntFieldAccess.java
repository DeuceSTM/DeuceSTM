package org.deuce.transaction.norec.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.Exclude;

/**
 * @author Pascal Felber
 */
@Exclude
public class IntFieldAccess extends FieldAccess {

	final private int value;

	public IntFieldAccess(Object reference, long field, int value) {
		super(reference, field);
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public void writeField() {
		UnsafeHolder.getUnsafe().putInt(reference, field, value);
	}

	@Override
	public boolean validate() {
		return value == UnsafeHolder.getUnsafe().getInt(reference, field);
	}
}