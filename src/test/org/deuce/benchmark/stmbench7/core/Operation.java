package org.deuce.benchmark.stmbench7.core;

import org.deuce.benchmark.stmbench7.OperationId;
import org.deuce.benchmark.stmbench7.annotations.ReadOnly;
import org.deuce.benchmark.stmbench7.annotations.ThreadLocal;
import org.deuce.benchmark.stmbench7.annotations.Transactional;

/**
 * Interface of a single operation (query, traversal 
 * or structural modification) of the STMBench7 benchmark.
 * Each operation is instantiated separately by each
 * thread, so the fields in a class implementing Operation
 * can be considered thread-local.
 */
@ThreadLocal
public interface Operation {

	@Transactional
    public int performOperation() throws OperationFailedException;
	
	@ReadOnly
	public OperationId getOperationId();
}
