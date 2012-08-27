package org.deuce.benchmark.stmbench7.operations;

import org.deuce.benchmark.stmbench7.OperationId;
import org.deuce.benchmark.stmbench7.Parameters;
import org.deuce.benchmark.stmbench7.Setup;
import org.deuce.benchmark.stmbench7.ThreadRandom;
import org.deuce.benchmark.stmbench7.annotations.ReadOnly;
import org.deuce.benchmark.stmbench7.annotations.Transactional;
import org.deuce.benchmark.stmbench7.backend.Index;
import org.deuce.benchmark.stmbench7.core.AtomicPart;
import org.deuce.benchmark.stmbench7.core.OperationFailedException;

/**
 * Query Q1 / Operation OP1 (see the specification).
 * Read-only, search on index.
 */
public class Query1 extends BaseOperation {

	Index<Integer,AtomicPart> partIdIndex;

	public Query1(Setup oo7setup) {
		this.partIdIndex = oo7setup.getAtomicPartIdIndex();
	}

	@Override
	@Transactional @ReadOnly
	public int performOperation() throws OperationFailedException {
		int count = 0;
		
		for(int i = 0; i < 10; i++) {
			int partId = ThreadRandom.nextInt(Parameters.MaxAtomicParts) + 1;
			AtomicPart part = partIdIndex.get(partId);

			if(part == null) continue;

			performOperationInAtomicPart(part);
			count++;
		}

		return count;
	}
	
	protected void performOperationInAtomicPart(AtomicPart atomicPart) {
		atomicPart.nullOperation();
	}
	
    @Override
    public OperationId getOperationId() {
    	return OperationId.OP1;
    }
}
