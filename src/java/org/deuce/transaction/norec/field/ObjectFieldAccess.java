package org.deuce.transaction.norec.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.Exclude;

/**
 * @author Pascal Felber
 */
@Exclude
public class ObjectFieldAccess extends FieldAccess {

	final private Object value;

	public ObjectFieldAccess(Object reference, long field, Object value) {
		super(reference, field);
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public void writeField() {
		UnsafeHolder.getUnsafe().putObject(reference, field, value);
	}

	@Override
	public boolean validate() {
		return value == UnsafeHolder.getUnsafe().getObject(reference, field);
	}
}