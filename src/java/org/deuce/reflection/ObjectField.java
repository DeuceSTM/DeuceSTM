package org.deuce.reflection;

public interface ObjectField<T,F>
{
	F get( T obj);
	void set( T obj, F value);
}
