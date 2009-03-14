package org.deuce.transaction.tl2.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.Exclude;

@Exclude
public class LongWriteFieldAccess extends WriteFieldAccess {

	private final long value;

	public LongWriteFieldAccess(long value, Object reference, long field) {
		super(reference, field);
		this.value = value;
	}

	@Override
	public void put() {
		UnsafeHolder.getUnsafe().putLong(reference, field, value);
	}

	public long getValue() {
		return value;
	}

}
