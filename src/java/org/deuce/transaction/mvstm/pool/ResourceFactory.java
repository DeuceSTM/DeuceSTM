package org.deuce.transaction.mvstm.pool;

public interface ResourceFactory<T>{
	T newInstance();
}
