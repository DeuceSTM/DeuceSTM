package org.deuce.transaction.norec.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.Exclude;

/**
 * @author Pascal Felber
 */
@Exclude
public class LongFieldAccess extends FieldAccess {

	final private long value;

	public LongFieldAccess(Object reference, long field, long value) {
		super(reference, field);
		this.value = value;
	}

	public long getValue() {
		return value;
	}

	@Override
	public void writeField() {
		UnsafeHolder.getUnsafe().putLong(reference, field, value);
	}

	@Override
	public boolean validate() {
		return value == UnsafeHolder.getUnsafe().getLong(reference, field);
	}
}