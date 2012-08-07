package org.deuce.transaction.jvstminnogc.pool;

public interface ResourceFactory<T>{
	T newInstance();
}
