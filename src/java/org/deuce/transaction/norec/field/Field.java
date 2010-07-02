package org.deuce.transaction.norec.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.Exclude;

import sun.misc.Unsafe;

/**
 * @author Pascal Felber
 */
@Exclude
public class Field {

	@Exclude
	static public enum Type {
		BYTE, BOOLEAN, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE, OBJECT
	}

	static public FieldAccess newFieldAccess(Object reference, long field, Type type) {
		Unsafe unsafe = UnsafeHolder.getUnsafe();
		switch (type) {
		case BYTE:
			return new ByteFieldAccess(reference, field, unsafe.getByte(reference, field));
		case BOOLEAN:
			return new BooleanFieldAccess(reference, field, unsafe.getBoolean(reference, field));
		case CHAR:
			return new CharFieldAccess(reference, field, unsafe.getChar(reference, field));
		case SHORT:
			return new ShortFieldAccess(reference, field, unsafe.getShort(reference, field));
		case INT:
			return new IntFieldAccess(reference, field, unsafe.getInt(reference, field));
		case LONG:
			return new LongFieldAccess(reference, field, unsafe.getLong(reference, field));
		case FLOAT:
			return new FloatFieldAccess(reference, field, unsafe.getFloat(reference, field));
		case DOUBLE:
			return new DoubleFieldAccess(reference, field, unsafe.getDouble(reference, field));
		case OBJECT:
			return new ObjectFieldAccess(reference, field, unsafe.getObject(reference, field));
		default:
			throw new RuntimeException("Unknown type " + type);
		}
	}

	static public Object getValue(Object reference, long field, Type type) {
		Unsafe unsafe = UnsafeHolder.getUnsafe();
		switch (type) {
		case BYTE:
			return unsafe.getByte(reference, field);
		case BOOLEAN:
			return unsafe.getBoolean(reference, field);
		case CHAR:
			return unsafe.getChar(reference, field);
		case SHORT:
			return unsafe.getShort(reference, field);
		case INT:
			return unsafe.getInt(reference, field);
		case LONG:
			return unsafe.getLong(reference, field);
		case FLOAT:
			return unsafe.getFloat(reference, field);
		case DOUBLE:
			return unsafe.getDouble(reference, field);
		case OBJECT:
			return unsafe.getObject(reference, field);
		default:
			throw new RuntimeException("Unknown type " + type);
		}
	}

	static public void putValue(Object reference, long field, Object value, Type type) {
		Unsafe unsafe = UnsafeHolder.getUnsafe();
		switch (type) {
		case BYTE:
			unsafe.putByte(reference, field, (Byte) value);
			break;
		case BOOLEAN:
			unsafe.putBoolean(reference, field, (Boolean) value);
			break;
		case CHAR:
			unsafe.putChar(reference, field, (Character) value);
			break;
		case SHORT:
			unsafe.putShort(reference, field, (Short) value);
			break;
		case INT:
			unsafe.putInt(reference, field, (Integer) value);
			break;
		case LONG:
			unsafe.putLong(reference, field, (Long) value);
			break;
		case FLOAT:
			unsafe.putFloat(reference, field, (Float) value);
			break;
		case DOUBLE:
			unsafe.putDouble(reference, field, (Double) value);
			break;
		case OBJECT:
			unsafe.putObject(reference, field, value);
			break;
		default:
			throw new RuntimeException("Unknown type " + type);
		}
	}
}