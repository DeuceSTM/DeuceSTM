package org.deuce.benchmark.stmbench7.operations;

import java.util.HashSet;

import org.deuce.benchmark.stmbench7.OperationId;
import org.deuce.benchmark.stmbench7.Setup;
import org.deuce.benchmark.stmbench7.core.AtomicPart;

/**
 * Traversal T3, variant (b) (see the specification).
 * Simple update, update on index, long.
 */
public class Traversal3b extends Traversal3a {

	public Traversal3b(Setup oo7setup) {
		super(oo7setup);
	}

	@Override
	protected int performOperationInAtomicPart(AtomicPart part, HashSet<AtomicPart> setOfVisitedPartIds) {
		updateBuildDate(part);
		return 1;
    }
	
    @Override
    public OperationId getOperationId() {
    	return OperationId.T3b;
    }
}
	
