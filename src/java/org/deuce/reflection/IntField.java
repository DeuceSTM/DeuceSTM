package org.deuce.reflection;

public interface IntField<T>
{
	int get( T obj);
	void set( T obj, int value);
}
