package org.deuce.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.deuce.transform.Exclude;

@Exclude
public class AddressUtil {

	/**
	 * Fetches the field direct address.
	 * @param fieldName field reference
	 * @return direct address
	 */
	public static long getAddress( Field field) {
		if( (field.getModifiers() & Modifier.STATIC) != 0){
			return UnsafeHolder.getUnsafe().staticFieldOffset(field);
		}else{
			return UnsafeHolder.getUnsafe().objectFieldOffset(field);
		}
	}

	/**
	 * Fetches base class handle
	 * @param fieldName field reference
	 * @return direct address
	 */
	public static Object staticFieldBase( Field field) {
		return UnsafeHolder.getUnsafe().staticFieldBase( field);
	}
	
	/**
	 * Fetches the array base offset
	 * @param arrClass array class
	 * @return offset
	 */
	public static int arrayBaseOffset( Class arrClass) {
		return UnsafeHolder.getUnsafe().arrayBaseOffset( arrClass);
	}
	
	/**
	 * Fetches the array index scale
	 * @param arrClass array class
	 * @return scale
	 */
	public static int arrayIndexScale( Class arrClass) {
		return UnsafeHolder.getUnsafe().arrayIndexScale( arrClass);
	}
}
