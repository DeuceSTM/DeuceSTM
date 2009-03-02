package org.deuce.reflection;

public interface BooleanField<T>
{
	boolean get( T obj);
	void set( T obj, boolean value);
}
