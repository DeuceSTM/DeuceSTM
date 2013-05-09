package org.deuce.utest.capmem;

import java.lang.reflect.Field;

import org.deuce.transform.Exclude;

@Exclude
class AvoidBarriers{
	public static Object getObjectValue(Object target, String fieldName){
		try {
			Field f = target.getClass().getDeclaredField(fieldName);
			return f.get(target);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public static int getIntValue(Object target, String fieldName){
		try {
			Field f = target.getClass().getDeclaredField(fieldName);
			return f.getInt(target);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	public static byte getByteValue(Object target, String fieldName){
		try {
			Field f = target.getClass().getDeclaredField(fieldName);
			return f.getByte(target);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	public static short getShortValue(Object target, String fieldName){
		try {
			Field f = target.getClass().getDeclaredField(fieldName);
			return f.getShort(target);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	public static long getLongValue(Object target, String fieldName){
		try {
			Field f = target.getClass().getDeclaredField(fieldName);
			return f.getLong(target);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
	}

}
