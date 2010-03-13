package org.deuce.benchmark.stmbench7;

import org.deuce.benchmark.stmbench7.annotations.NonAtomic;
import org.deuce.benchmark.stmbench7.annotations.ThreadLocal;
import org.deuce.benchmark.stmbench7.core.OperationFailedException;

/**
 * An interface representing a class that executes a given operation.
 * Can set up a transaction and handle aborts.
 */
@NonAtomic
@ThreadLocal
public interface OperationExecutor {

	int execute() throws OperationFailedException;
	int getLastOperationTimestamp();
}
