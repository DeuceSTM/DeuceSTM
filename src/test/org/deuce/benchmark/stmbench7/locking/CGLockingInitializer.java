package org.deuce.benchmark.stmbench7.locking;

import org.deuce.benchmark.stmbench7.OperationExecutorFactory;
import org.deuce.benchmark.stmbench7.impl.NoSynchronizationInitializer;

/**
 * An initializer for the coarse-grained locking
 * thread synchronization.
 */
public class CGLockingInitializer extends NoSynchronizationInitializer {

	@Override
	public OperationExecutorFactory createOperationExecutorFactory() {
		return new CGLockingOperationExecutorFactory();
	}
}
