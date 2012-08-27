package org.deuce.benchmark.stmbench7.impl;

import org.deuce.benchmark.stmbench7.OperationExecutor;
import org.deuce.benchmark.stmbench7.core.Operation;
import org.deuce.benchmark.stmbench7.core.OperationFailedException;

/**
 * Default implementation of an OperationExecutor.
 * Does not provide any thread-safety.
 */
public class DefaultOperationExecutor implements OperationExecutor {

	private static int clock = 0;
	
	private final Operation op;
	private int lastTimestamp = 0;
	
	public DefaultOperationExecutor(Operation op) {
		this.op = op;
	}
	
	public int execute() throws OperationFailedException {
		lastTimestamp = clock++;
		return op.performOperation();
	}

	public int getLastOperationTimestamp() {
		return lastTimestamp;
	}	
}
