package org.deuce.transaction.jvstminplace.pool;

public interface ResourceFactory<T>{
	T newInstance();
}
