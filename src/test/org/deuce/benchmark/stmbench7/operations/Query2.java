package org.deuce.benchmark.stmbench7.operations;

import org.deuce.benchmark.stmbench7.OperationId;
import org.deuce.benchmark.stmbench7.Parameters;
import org.deuce.benchmark.stmbench7.Setup;
import org.deuce.benchmark.stmbench7.annotations.ReadOnly;
import org.deuce.benchmark.stmbench7.annotations.Transactional;
import org.deuce.benchmark.stmbench7.backend.Index;
import org.deuce.benchmark.stmbench7.backend.LargeSet;
import org.deuce.benchmark.stmbench7.core.AtomicPart;
import org.deuce.benchmark.stmbench7.core.OperationFailedException;

/**
 * Query Q2 / Operation OP2 (see the specification).
 * Read-only, range query on index.
 */
public class Query2 extends BaseOperation {

    protected Index<Integer,LargeSet<AtomicPart>> partBuildDateIndex;
    protected Integer minAtomicDate, maxAtomicDate;
    
    public Query2(Setup oo7setup) {
    	this(oo7setup, 1);
    }

    protected Query2(Setup oo7setup, int percent) {
    	this.partBuildDateIndex = oo7setup.getAtomicPartBuildDateIndex();
    	this.maxAtomicDate = Parameters.MaxAtomicDate;
    	this.minAtomicDate = Parameters.MaxAtomicDate - 
    				percent * (Parameters.MaxAtomicDate - Parameters.MinAtomicDate) / 100;
    }

    @Override
	@Transactional @ReadOnly
    public int performOperation() throws OperationFailedException {
    	Iterable<LargeSet<AtomicPart>> partSets = partBuildDateIndex.getRange(minAtomicDate, maxAtomicDate);
    	int count = 0;

    	for(LargeSet<AtomicPart> partSet : partSets) {
    		for(AtomicPart part : partSet) {
    			performOperationInAtomicPart(part);
    			count++;
    		}
    	}

    	return count;
    }
    
	protected void performOperationInAtomicPart(AtomicPart atomicPart) {
		atomicPart.nullOperation();
	}
	
    @Override
    public OperationId getOperationId() {
    	return OperationId.OP2;
    }
}
