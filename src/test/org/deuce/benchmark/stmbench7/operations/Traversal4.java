package org.deuce.benchmark.stmbench7.operations;

import java.util.HashSet;

import org.deuce.benchmark.stmbench7.OperationId;
import org.deuce.benchmark.stmbench7.Setup;
import org.deuce.benchmark.stmbench7.core.AtomicPart;
import org.deuce.benchmark.stmbench7.core.CompositePart;
import org.deuce.benchmark.stmbench7.core.Document;
import org.deuce.benchmark.stmbench7.core.RuntimeError;

/**
 * Traversal T4 (see the specification).
 * Read-only, long.
 */
public class Traversal4 extends Traversal1 {

    public Traversal4(Setup oo7setup) {
    	super(oo7setup);
    }

    @Override
    protected int traverse(CompositePart component) {
    	Document documentation = component.getDocumentation();
    	return traverse(documentation);
    }
    
    protected int traverse(Document documentation) {
    	return documentation.searchText('I');
    }

    @Override
    protected int traverse(AtomicPart part, HashSet<AtomicPart> setOfVisitedPartIds) {
    	throw new RuntimeError("T4: traverse(AtomicPart, HashSet<AtomicPart>) called!");
    }

    @Override
    protected int performOperationInAtomicPart(AtomicPart part, HashSet<AtomicPart> setOfVisitedPartIds) {
    	throw new RuntimeError("T4: performOperationInAtomicPart(..) called!");
    }
    
    @Override
    public OperationId getOperationId() {
    	return OperationId.T4;
    }
}
