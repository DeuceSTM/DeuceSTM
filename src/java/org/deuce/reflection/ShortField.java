package org.deuce.reflection;

public interface ShortField<T>
{
	short get( T obj);
	void set( T obj, short value);
}
