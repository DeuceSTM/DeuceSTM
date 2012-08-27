package org.deuce.benchmark.stmbench7.operations;

import org.deuce.benchmark.stmbench7.OperationId;
import org.deuce.benchmark.stmbench7.Setup;
import org.deuce.benchmark.stmbench7.annotations.Transactional;
import org.deuce.benchmark.stmbench7.annotations.Update;
import org.deuce.benchmark.stmbench7.core.CompositePart;
import org.deuce.benchmark.stmbench7.core.OperationFailedException;

/**
 * Operation OP14 (see the specification).
 * Simple update, search on index.
 */
public class Operation14 extends Operation8 {

	public Operation14(Setup oo7setup) {
		super(oo7setup);
	}
    
	@Override
	@Transactional @Update
	public int performOperation() throws OperationFailedException {
    	return super.performOperation();
	}
    
	@Override
	protected void performOperationInComponent(CompositePart component) {
		component.updateBuildDate();
	}
	
    @Override
    public OperationId getOperationId() {
    	return OperationId.OP14;
    }
}
