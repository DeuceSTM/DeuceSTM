package org.deuce.benchmark.stmbench7.operations;

import org.deuce.benchmark.stmbench7.OperationId;
import org.deuce.benchmark.stmbench7.Parameters;
import org.deuce.benchmark.stmbench7.Setup;
import org.deuce.benchmark.stmbench7.ThreadRandom;
import org.deuce.benchmark.stmbench7.annotations.ReadOnly;
import org.deuce.benchmark.stmbench7.annotations.Transactional;
import org.deuce.benchmark.stmbench7.backend.Index;
import org.deuce.benchmark.stmbench7.core.Assembly;
import org.deuce.benchmark.stmbench7.core.BaseAssembly;
import org.deuce.benchmark.stmbench7.core.ComplexAssembly;
import org.deuce.benchmark.stmbench7.core.OperationFailedException;

/**
 * Operation OP7 (see the specification).
 * Read-only, search on index.
 */
public class Operation7 extends BaseOperation {

	protected Index<Integer,BaseAssembly> baseAssemblyIdIndex;
	
	public Operation7(Setup oo7setup) {
		this.baseAssemblyIdIndex = oo7setup.getBaseAssemblyIdIndex();
	}
	
	@Override
	@Transactional @ReadOnly
	public int performOperation() throws OperationFailedException {
		int baseAssemblyId = ThreadRandom.nextInt(Parameters.MaxBaseAssemblies) +1;
		BaseAssembly baseAssembly = baseAssemblyIdIndex.get(baseAssemblyId);
		if(baseAssembly == null) throw new OperationFailedException();
		
		ComplexAssembly superAssembly = baseAssembly.getSuperAssembly();
		
		int count = 0;
		for(Assembly siblingAssembly : superAssembly.getSubAssemblies()) {
			performOperationInBaseAssembly((BaseAssembly)siblingAssembly);
			count++;
		}
		
		return count;
	}
	
	protected void performOperationInBaseAssembly(BaseAssembly assembly) {
		assembly.nullOperation();
	}
	
    @Override
    public OperationId getOperationId() {
    	return OperationId.OP7;
    }
}
