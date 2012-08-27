package org.deuce.benchmark.stmbench7.operations;

import org.deuce.benchmark.stmbench7.OperationId;
import org.deuce.benchmark.stmbench7.Setup;
import org.deuce.benchmark.stmbench7.annotations.Transactional;
import org.deuce.benchmark.stmbench7.annotations.Update;
import org.deuce.benchmark.stmbench7.core.AtomicPart;
import org.deuce.benchmark.stmbench7.core.OperationFailedException;

/**
 * Short traversal ST6 (see the specification).
 * Simple update, short.
 */
public class ShortTraversal6 extends ShortTraversal1 {

	public ShortTraversal6(Setup oo7setup) {
		super(oo7setup);
	}

    @Override
	@Transactional @Update
	public int performOperation() throws OperationFailedException {
    	return super.performOperation();
	}
    
    @Override
	protected int traverse(AtomicPart atomicPart) {
		atomicPart.swapXY();
		return super.traverse(atomicPart);
	}
    
    @Override
    public OperationId getOperationId() {
    	return OperationId.ST6;
    }
}
