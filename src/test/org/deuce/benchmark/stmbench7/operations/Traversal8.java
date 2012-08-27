package org.deuce.benchmark.stmbench7.operations;

import org.deuce.benchmark.stmbench7.OperationId;
import org.deuce.benchmark.stmbench7.Setup;
import org.deuce.benchmark.stmbench7.annotations.ReadOnly;
import org.deuce.benchmark.stmbench7.annotations.Transactional;
import org.deuce.benchmark.stmbench7.core.Manual;
import org.deuce.benchmark.stmbench7.core.Module;

/**
 * Traversal T8 / Operation OP4 (see the specification).
 * Read-only.
 */
public class Traversal8 extends BaseOperation {

	protected Module module;

	public Traversal8(Setup oo7setup) {
		this.module = oo7setup.getModule();
	}

	@Override
	@Transactional @ReadOnly
	public int performOperation() {
		Manual manual = module.getManual();
		return traverse(manual);
	}

	protected int traverse(Manual manual) {
		return manual.countOccurences('I');
	}
	
    @Override
    public OperationId getOperationId() {
    	return OperationId.OP4;
    }
}
