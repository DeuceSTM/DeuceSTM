package org.deuce.reflection;

public interface DoubleField<T>
{
	double get( T obj);
	void set( T obj, double value);
}
