package org.deuce.transaction.tl2.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.Exclude;

@Exclude
public class CharWriteFieldAccess extends WriteFieldAccess {

	private final char value;

	public CharWriteFieldAccess(char value, Object reference, long field) {
		super(reference, field);
		this.value = value;
	}

	@Override
	public void put() {
		UnsafeHolder.getUnsafe().putCharVolatile(reference, field, value);
	}

	public char getValue() {
		return value;
	}

}
