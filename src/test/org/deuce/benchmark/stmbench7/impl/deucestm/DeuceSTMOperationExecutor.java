package org.deuce.benchmark.stmbench7.impl.deucestm;

import org.deuce.benchmark.stmbench7.OperationExecutor;
import org.deuce.benchmark.stmbench7.core.Operation;
import org.deuce.benchmark.stmbench7.core.OperationFailedException;

public class DeuceSTMOperationExecutor implements OperationExecutor {

	private final Operation op;
	
	public DeuceSTMOperationExecutor(Operation op) {
		this.op = op;
	}
	
	@Override
	@org.deuce.Atomic
	public int execute() throws OperationFailedException {
		return op.performOperation();
	}

	@Override
	public int getLastOperationTimestamp() {
		return 0;
	}

}
