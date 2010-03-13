package org.deuce.benchmark.stmbench7;

import org.deuce.benchmark.stmbench7.annotations.NonAtomic;
import org.deuce.benchmark.stmbench7.backend.BackendFactory;
import org.deuce.benchmark.stmbench7.core.DesignObjFactory;

/**
 * Creates factories suitable for a given synchonization method.
 * Default implementations are provided for coarse-grained and
 * medium-grained locking, as well as for single-threaded executions
 * (i.e., without any thread synchronization).
 */
@NonAtomic
public interface SynchMethodInitializer {

	public DesignObjFactory createDesignObjFactory();
	public BackendFactory createBackendFactory();
	public OperationExecutorFactory createOperationExecutorFactory();
	public ThreadFactory createThreadFactory();
}
