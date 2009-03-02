package org.deuce.reflection;

public interface LongField<T>
{
	long get( T obj);
	void set( T obj, long value);
}
