package org.deuce.transaction.tl2.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.Exclude;

import sun.misc.Unsafe;

@Exclude
public class WriteFieldAccess extends ReadFieldAccess{

	@Exclude
	static public enum Type{ BYTE, BOOLEAN, CHAR, SHORT, INT,
		LONG, FLOAT, DOUBLE, OBJECT} 

	final private Type type;

	public WriteFieldAccess(Object reference, long field, Type type) {
		super(reference, field);

		this.type = type;
	}

	public void put( Object value) {
		Unsafe unsafe = UnsafeHolder.getUnsafe();
		switch( type) {
		case BYTE:
			unsafe.putByteVolatile(reference, field, (Byte)value);
			break;
		case BOOLEAN:
			unsafe.putBooleanVolatile(reference, field, (Boolean)value);
			break;
		case CHAR:
			unsafe.putCharVolatile(reference, field, (Character)value);
			break;
		case SHORT:
			unsafe.putShortVolatile(reference, field, (Short)value);
			break;
		case INT:
			unsafe.putIntVolatile(reference, field, (Integer)value);
			break;
		case LONG:
			unsafe.putLongVolatile(reference, field, (Long)value);
			break;
		case FLOAT:
			unsafe.putFloatVolatile(reference, field, (Float)value);
			break;
		case DOUBLE:
			unsafe.putDoubleVolatile(reference, field, (Double)value);
			break;
		case OBJECT:
			unsafe.putObjectVolatile(reference, field, value);
			break;
		}
	}

}