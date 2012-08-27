package org.deuce.benchmark.stmbench7;

import org.deuce.benchmark.stmbench7.annotations.NonAtomic;
import org.deuce.benchmark.stmbench7.core.Operation;
import org.deuce.benchmark.stmbench7.core.OperationFailedException;
import org.deuce.benchmark.stmbench7.core.RuntimeError;

/**
 * Creates an OperationExecutor object, which is used
 * to execute the benchmark operations. For the default
 * implementation, see stmbench7.impl.DefaultOperationExecutorFactory.
 */
@NonAtomic
public abstract class OperationExecutorFactory {

	public static OperationExecutorFactory instance = null;
	
	public static void setInstance(OperationExecutorFactory newInstance) {
		instance = newInstance;
	}
	
	public abstract OperationExecutor createOperationExecutor(Operation op);
	
	public static void executeSequentialOperation(final Operation op) throws InterruptedException {
		Thread opThread = ThreadFactory.instance.createThread(new Runnable() {
			public void run() {
				OperationExecutor operationExecutor = 
					instance.createOperationExecutor(op);
				try {
					operationExecutor.execute();
				}
				catch(OperationFailedException e) {
					throw new RuntimeError("Unexpected failure of a sequential operation " + op);
				}
				
			}
		});
		opThread.start();
		opThread.join();
	}
}
