package org.deuce.benchmark.stmbench7.impl;

import org.deuce.benchmark.stmbench7.OperationExecutor;
import org.deuce.benchmark.stmbench7.OperationExecutorFactory;
import org.deuce.benchmark.stmbench7.annotations.Immutable;
import org.deuce.benchmark.stmbench7.core.Operation;

/**
 * Default implementation of an OperationExecutorFactory.
 * It creates an OperationExecutor that does not provide
 * any synchronization between threads.
 */
@Immutable
public class DefaultOperationExecutorFactory extends OperationExecutorFactory {

	public OperationExecutor createOperationExecutor(Operation op) {
		return new DefaultOperationExecutor(op);
	}
}
