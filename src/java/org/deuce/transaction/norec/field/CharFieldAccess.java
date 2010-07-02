package org.deuce.transaction.norec.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.Exclude;

/**
 * @author Pascal Felber
 */
@Exclude
public class CharFieldAccess extends FieldAccess {

	final private char value;

	public CharFieldAccess(Object reference, long field, char value) {
		super(reference, field);
		this.value = value;
	}

	public char getValue() {
		return value;
	}

	@Override
	public void writeField() {
		UnsafeHolder.getUnsafe().putChar(reference, field, value);
	}

	@Override
	public boolean validate() {
		return value == UnsafeHolder.getUnsafe().getChar(reference, field);
	}
}