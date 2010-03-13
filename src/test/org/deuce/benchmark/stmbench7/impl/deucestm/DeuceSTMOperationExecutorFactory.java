package org.deuce.benchmark.stmbench7.impl.deucestm;

import org.deuce.benchmark.stmbench7.OperationExecutor;
import org.deuce.benchmark.stmbench7.OperationExecutorFactory;
import org.deuce.benchmark.stmbench7.core.Operation;
import org.deuce.benchmark.stmbench7.impl.DefaultOperationExecutor;

public class DeuceSTMOperationExecutorFactory extends OperationExecutorFactory {

	@Override
	public OperationExecutor createOperationExecutor(Operation op) {
		if(op.getOperationId() != null)
			return new DeuceSTMOperationExecutor(op);
		return new DefaultOperationExecutor(op);
	}

}
