package org.deuce.transaction.tl2.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.Exclude;

@Exclude
public class ObjectWriteFieldAccess extends WriteFieldAccess {

	private final Object value;

	public ObjectWriteFieldAccess(Object value, Object reference, long field) {
		super(reference, field);
		this.value = value;
	}

	@Override
	public void put() {
		UnsafeHolder.getUnsafe().putObjectVolatile(reference, field, value);
	}

	public Object getValue() {
		return value;
	}
}
