package org.deuce.benchmark.stmbench7.operations;

import org.deuce.benchmark.stmbench7.OperationId;
import org.deuce.benchmark.stmbench7.Setup;
import org.deuce.benchmark.stmbench7.core.Manual;

/**
 * Traversal T9 / Operation OP5 (see the specification).
 * Read-only.
 */
public class Traversal9 extends Traversal8 {

    public Traversal9(Setup oo7setup) {
    	super(oo7setup);
    }

    @Override
    protected int traverse(Manual manual) {
    	return manual.checkFirstLastCharTheSame();
    }
    
    @Override
    public OperationId getOperationId() {
    	return OperationId.OP5;
    }
}
