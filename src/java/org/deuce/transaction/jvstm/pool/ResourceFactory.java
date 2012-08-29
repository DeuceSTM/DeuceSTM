package org.deuce.transaction.jvstm.pool;

public interface ResourceFactory<T>{
	T newInstance();
}
