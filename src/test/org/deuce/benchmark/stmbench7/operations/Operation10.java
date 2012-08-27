package org.deuce.benchmark.stmbench7.operations;

import org.deuce.benchmark.stmbench7.OperationId;
import org.deuce.benchmark.stmbench7.Setup;
import org.deuce.benchmark.stmbench7.annotations.Transactional;
import org.deuce.benchmark.stmbench7.annotations.Update;
import org.deuce.benchmark.stmbench7.core.AtomicPart;
import org.deuce.benchmark.stmbench7.core.OperationFailedException;

/**
 * Operation OP10 (see the specification).
 * Simple update, range query on index.
 */
public class Operation10 extends Query2 {

    public Operation10(Setup oo7setup) {
    	super(oo7setup, 1);
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
    	return OperationId.OP10;
    }
}
