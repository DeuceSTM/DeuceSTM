package org.deuce.transaction.tl2.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.Exclude;

@Exclude
public class ByteWriteFieldAccess extends WriteFieldAccess {

	private final byte value;

	public ByteWriteFieldAccess(byte value, Object reference, long field) {
		super(reference, field);
		this.value = value;
	}

	@Override
	public void put() {
		UnsafeHolder.getUnsafe().putByteVolatile(reference, field, value);
	}

	public byte getValue() {
		return value;
	}

}
