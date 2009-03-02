package org.deuce.reflection;

public interface ByteField<T>
{
	byte get( T obj);
	void set( T obj, byte value);
}
