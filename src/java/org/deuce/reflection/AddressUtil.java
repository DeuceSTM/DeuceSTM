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
	 * @throws NoSuchFieldException if a field with the specified name is not found.
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
	 * @throws NoSuchFieldException if a field with the specified name is not found.
	 */
	public static Object staticFieldBase( Field field) {
		return UnsafeHolder.getUnsafe().staticFieldBase( field);
	}
}
