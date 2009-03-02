package org.deuce.reflection;

public interface CharField<T>
{
	char get( T obj);
	void set( T obj, char value);
}
