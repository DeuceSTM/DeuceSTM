package org.deuce.transaction.norec.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.Exclude;

/**
 * @author Pascal Felber
 */
@Exclude
public class ShortFieldAccess extends FieldAccess {

	final private short value;

	public ShortFieldAccess(Object reference, long field, short value) {
		super(reference, field);
		this.value = value;
	}

	public short getValue() {
		return value;
	}

	@Override
	public void writeField() {
		UnsafeHolder.getUnsafe().putShort(reference, field, value);
	}

	@Override
	public boolean validate() {
		return value == UnsafeHolder.getUnsafe().getShort(reference, field);
	}
}