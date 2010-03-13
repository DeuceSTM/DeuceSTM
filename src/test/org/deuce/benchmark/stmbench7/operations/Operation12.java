package org.deuce.benchmark.stmbench7.operations;

import org.deuce.benchmark.stmbench7.OperationId;
import org.deuce.benchmark.stmbench7.Setup;
import org.deuce.benchmark.stmbench7.annotations.Transactional;
import org.deuce.benchmark.stmbench7.annotations.Update;
import org.deuce.benchmark.stmbench7.core.ComplexAssembly;
import org.deuce.benchmark.stmbench7.core.OperationFailedException;

/**
 * Operation OP12 (see the specification).
 * Simple update, search on index.
 */
public class Operation12 extends Operation6 {

	public Operation12(Setup oo7setup) {
		super(oo7setup);
	}
    
	@Override
	@Transactional @Update
	public int performOperation() throws OperationFailedException {
    	return super.performOperation();
	}
    
	@Override
	protected void performOperationInComplexAssembly(ComplexAssembly assembly) {
		assembly.updateBuildDate();
	}
	
    @Override
    public OperationId getOperationId() {
    	return OperationId.OP12;
    }
}
