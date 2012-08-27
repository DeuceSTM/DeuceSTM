package org.deuce.benchmark.stmbench7.impl;

import org.deuce.benchmark.stmbench7.OperationExecutorFactory;
import org.deuce.benchmark.stmbench7.SynchMethodInitializer;
import org.deuce.benchmark.stmbench7.ThreadFactory;
import org.deuce.benchmark.stmbench7.annotations.Immutable;
import org.deuce.benchmark.stmbench7.backend.BackendFactory;
import org.deuce.benchmark.stmbench7.core.DesignObjFactory;
import org.deuce.benchmark.stmbench7.impl.backend.BackendFactoryImpl;
import org.deuce.benchmark.stmbench7.impl.core.DesignObjFactoryImpl;

/**
 * An initializer that provides non-thread-safe (default)
 * factories. Should normally be used only with a single thread.
 */
@Immutable
public class NoSynchronizationInitializer implements SynchMethodInitializer {

	public BackendFactory createBackendFactory() {
		return new BackendFactoryImpl();
	}

	public DesignObjFactory createDesignObjFactory() {
		return new DesignObjFactoryImpl();
	}

	public OperationExecutorFactory createOperationExecutorFactory() {
		return new DefaultOperationExecutorFactory();
	}

	public ThreadFactory createThreadFactory() {
		return new DefaultThreadFactory();
	}
}
