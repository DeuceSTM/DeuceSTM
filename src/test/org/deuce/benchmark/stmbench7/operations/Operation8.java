package org.deuce.benchmark.stmbench7.operations;

import org.deuce.benchmark.stmbench7.OperationId;
import org.deuce.benchmark.stmbench7.Parameters;
import org.deuce.benchmark.stmbench7.Setup;
import org.deuce.benchmark.stmbench7.ThreadRandom;
import org.deuce.benchmark.stmbench7.annotations.ReadOnly;
import org.deuce.benchmark.stmbench7.annotations.Transactional;
import org.deuce.benchmark.stmbench7.backend.Index;
import org.deuce.benchmark.stmbench7.core.BaseAssembly;
import org.deuce.benchmark.stmbench7.core.CompositePart;
import org.deuce.benchmark.stmbench7.core.OperationFailedException;

/**
 * Operation OP8 (see the specification).
 * Read-only, search on index.
 */
public class Operation8 extends BaseOperation {
	
	protected Index<Integer,BaseAssembly> baseAssemblyIdIndex;
	
	public Operation8(Setup oo7setup) {
		this.baseAssemblyIdIndex = oo7setup.getBaseAssemblyIdIndex();
	}
	
	@Override
	@Transactional @ReadOnly
	public int performOperation() throws OperationFailedException {
		int baseAssemblyId = ThreadRandom.nextInt(Parameters.MaxBaseAssemblies) +1;
		BaseAssembly baseAssembly = baseAssemblyIdIndex.get(baseAssemblyId);
		if(baseAssembly == null) throw new OperationFailedException();
		
		int count = 0;
		for(CompositePart component : baseAssembly.getComponents()) {
			performOperationInComponent(component);
			count++;
		}
		
		return count;
	}
	
	protected void performOperationInComponent(CompositePart component) {
		component.nullOperation();
	}
	
    @Override
    public OperationId getOperationId() {
    	return OperationId.OP8;
    }
}
