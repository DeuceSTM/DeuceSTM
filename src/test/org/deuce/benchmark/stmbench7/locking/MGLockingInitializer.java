package org.deuce.benchmark.stmbench7.locking;

import org.deuce.benchmark.stmbench7.OperationExecutorFactory;
import org.deuce.benchmark.stmbench7.core.DesignObjFactory;
import org.deuce.benchmark.stmbench7.impl.NoSynchronizationInitializer;

/**
 * An initializer for the medium-grained locking synchronization
 * method.
 */
public class MGLockingInitializer extends NoSynchronizationInitializer {

	public DesignObjFactory createDesignObjFactory() {
		return new MGLockingDesignObjFactory();
	}

	public OperationExecutorFactory createOperationExecutorFactory() {
		return new MGLockingOperationExecutorFactory();
	}
}
