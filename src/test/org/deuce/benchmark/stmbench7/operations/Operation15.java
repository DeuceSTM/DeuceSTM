package org.deuce.benchmark.stmbench7.operations;

import org.deuce.benchmark.stmbench7.OperationId;
import org.deuce.benchmark.stmbench7.Setup;
import org.deuce.benchmark.stmbench7.annotations.Transactional;
import org.deuce.benchmark.stmbench7.annotations.Update;
import org.deuce.benchmark.stmbench7.backend.Index;
import org.deuce.benchmark.stmbench7.backend.LargeSet;
import org.deuce.benchmark.stmbench7.core.AtomicPart;
import org.deuce.benchmark.stmbench7.core.OperationFailedException;

/**
 * Operation OP15 (see the specification).
 * Simple update, search and update on index.
 */
public class Operation15 extends Query1 {

	protected Index<Integer,LargeSet<AtomicPart>> partBuildDateIndex;
	
	public Operation15(Setup oo7setup) {
		super(oo7setup);
		this.partBuildDateIndex = oo7setup.getAtomicPartBuildDateIndex();
	}
	
    @Override
	@Transactional @Update
	public int performOperation() throws OperationFailedException {
    	return super.performOperation();
	}
    
    @Override
	protected void performOperationInAtomicPart(AtomicPart atomicPart) {
		removeAtomicPartFromBuildDateIndex(partBuildDateIndex, atomicPart);
		atomicPart.updateBuildDate();
		addAtomicPartToBuildDateIndex(partBuildDateIndex, atomicPart);
	}
    
    @Override
    public OperationId getOperationId() {
    	return OperationId.OP15;
    }
}
