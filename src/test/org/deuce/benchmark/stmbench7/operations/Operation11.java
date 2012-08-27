package org.deuce.benchmark.stmbench7.operations;

import org.deuce.benchmark.stmbench7.OperationId;
import org.deuce.benchmark.stmbench7.Setup;
import org.deuce.benchmark.stmbench7.annotations.Transactional;
import org.deuce.benchmark.stmbench7.annotations.Update;
import org.deuce.benchmark.stmbench7.core.Manual;
import org.deuce.benchmark.stmbench7.core.RuntimeError;

/**
 * Operation OP11 (see the specification).
 * Simple update.
 */
public class Operation11 extends Traversal8 {

	public Operation11(Setup oo7setup) {
		super(oo7setup);
	}
	
	@Override
	@Transactional @Update
	public int performOperation() {
    	return super.performOperation();
	}
	
	@Override
	protected int traverse(Manual manual) {
		if(manual.startsWith('I')) return manual.replaceChar('I', 'i');
		if(manual.startsWith('i')) return manual.replaceChar('i', 'I');
		
		throw new RuntimeError("OP11: unexpected Manual.text!");
	}
	
    @Override
    public OperationId getOperationId() {
    	return OperationId.OP11;
    }
}
