package org.deuce.transaction.lsa.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transaction.lsa.field.WriteFieldAccess.Type;
import org.deuce.transform.Exclude;

import sun.misc.Unsafe;

@Exclude
public class Field {
	final protected Object reference;
	final protected long field;

	public Field(Object reference, long field) {
		this.reference = reference;
		this.field = field;
	}

	@Override
	public boolean equals(Object obj) {
		Field other = (Field) obj;
		return reference == other.reference && field == other.field;
	}

	static public Object getValue(Object reference, long field, Type type) {
		Unsafe unsafe = UnsafeHolder.getUnsafe();
		switch (type) {
		case BYTE:
			return unsafe.getByteVolatile(reference, field);
		case CHAR:
			return unsafe.getCharVolatile(reference, field);
		case SHORT:
			return unsafe.getShortVolatile(reference, field);
		case INT:
			return unsafe.getIntVolatile(reference, field);
		case LONG:
			return unsafe.getLongVolatile(reference, field);
		case FLOAT:
			return unsafe.getFloatVolatile(reference, field);
		case DOUBLE:
			return unsafe.getDoubleVolatile(reference, field);
		case OBJECT:
			return unsafe.getObjectVolatile(reference, field);
		}
		return null;
	}

	static public void putValue(Object reference, long field, Object value, Type type) {
		Unsafe unsafe = UnsafeHolder.getUnsafe();
		switch (type) {
		case BYTE:
			unsafe.putByteVolatile(reference, field, (Byte) value);
			break;
		case CHAR:
			unsafe.putCharVolatile(reference, field, (Character) value);
			break;
		case SHORT:
			unsafe.putShortVolatile(reference, field, (Short) value);
			break;
		case INT:
			unsafe.putIntVolatile(reference, field, (Integer) value);
			break;
		case LONG:
			unsafe.putLongVolatile(reference, field, (Long) value);
			break;
		case FLOAT:
			unsafe.putFloatVolatile(reference, field, (Float) value);
			break;
		case DOUBLE:
			unsafe.putDoubleVolatile(reference, field, (Double) value);
			break;
		case OBJECT:
			unsafe.putObjectVolatile(reference, field, value);
			break;
		}
	}
}
