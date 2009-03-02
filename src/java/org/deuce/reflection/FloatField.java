package org.deuce.reflection;

public interface FloatField<T>
{
	float get( T obj);
	void set( T obj, float value);
}
