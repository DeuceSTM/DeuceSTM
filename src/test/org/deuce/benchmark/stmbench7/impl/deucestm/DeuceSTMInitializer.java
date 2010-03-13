package org.deuce.benchmark.stmbench7.impl.deucestm;

import org.deuce.benchmark.stmbench7.OperationExecutorFactory;
import org.deuce.benchmark.stmbench7.impl.NoSynchronizationInitializer;

public class DeuceSTMInitializer extends NoSynchronizationInitializer {

	@Override
	public OperationExecutorFactory createOperationExecutorFactory() {
		return new DeuceSTMOperationExecutorFactory();
	}
}
