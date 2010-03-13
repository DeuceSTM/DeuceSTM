package org.deuce.benchmark.stmbench7.operations;

import org.deuce.benchmark.stmbench7.OperationId;
import org.deuce.benchmark.stmbench7.Setup;

/**
 * Query Q3 / Operation OP3 (see the specification).
 * Read-only, range query on index.
 */
public class Query3 extends Query2 {

    public Query3(Setup oo7setup) {
    	super(oo7setup, 10);
    }
    
    @Override
    public OperationId getOperationId() {
    	return OperationId.OP3;
    }
}
