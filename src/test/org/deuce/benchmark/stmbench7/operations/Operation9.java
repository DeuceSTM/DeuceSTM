package org.deuce.benchmark.stmbench7.operations;

import org.deuce.benchmark.stmbench7.OperationId;
import org.deuce.benchmark.stmbench7.Setup;
import org.deuce.benchmark.stmbench7.annotations.Transactional;
import org.deuce.benchmark.stmbench7.annotations.Update;
import org.deuce.benchmark.stmbench7.core.AtomicPart;
import org.deuce.benchmark.stmbench7.core.OperationFailedException;

/**
 * Operation OP9 (see the specification).
 * Simple update, search on index.
 */
public class Operation9 extends Query1 {

	public Operation9(Setup oo7setup) {
		super(oo7setup);
	}
    
	@Override
	@Transactional @Update
	public int performOperation() throws OperationFailedException {
    	return super.performOperation();
	}
	
	@Override
	protected void performOperationInAtomicPart(AtomicPart atomicPart) {
		atomicPart.swapXY();
	}
	
    @Override
    public OperationId getOperationId() {
    	return OperationId.OP9;
    }
}
