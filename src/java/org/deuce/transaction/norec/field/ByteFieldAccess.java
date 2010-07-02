package org.deuce.transaction.norec.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.Exclude;

/**
 * @author Pascal Felber
 */
@Exclude
public class ByteFieldAccess extends FieldAccess {

	final private byte value;

	public ByteFieldAccess(Object reference, long field, byte value) {
		super(reference, field);
		this.value = value;
	}

	public byte getValue() {
		return value;
	}

	@Override
	public void writeField() {
		UnsafeHolder.getUnsafe().putByte(reference, field, value);
	}

	@Override
	public boolean validate() {
		return value == UnsafeHolder.getUnsafe().getByte(reference, field);
	}
}