package org.deuce.benchmark.stmbench7.operations;

import org.deuce.benchmark.stmbench7.OperationId;
import org.deuce.benchmark.stmbench7.Parameters;
import org.deuce.benchmark.stmbench7.Setup;
import org.deuce.benchmark.stmbench7.ThreadRandom;
import org.deuce.benchmark.stmbench7.annotations.ReadOnly;
import org.deuce.benchmark.stmbench7.annotations.Transactional;
import org.deuce.benchmark.stmbench7.backend.Index;
import org.deuce.benchmark.stmbench7.core.Assembly;
import org.deuce.benchmark.stmbench7.core.ComplexAssembly;
import org.deuce.benchmark.stmbench7.core.OperationFailedException;

/**
 * Operation OP6 (see the specification).
 * Read-only, search on index.
 */
public class Operation6 extends BaseOperation {

	protected Index<Integer,ComplexAssembly> complexAssemblyIdIndex;

	public Operation6(Setup oo7setup) {
		this.complexAssemblyIdIndex = oo7setup.getComplexAssemblyIdIndex();
	}
	
	@Override
	@Transactional @ReadOnly
	public int performOperation() throws OperationFailedException {
		int complexAssemblyId = ThreadRandom.nextInt(Parameters.MaxComplexAssemblies) +1;
		ComplexAssembly complexAssembly = complexAssemblyIdIndex.get(complexAssemblyId);
		if(complexAssembly == null) throw new OperationFailedException();
		
		ComplexAssembly superAssembly = complexAssembly.getSuperAssembly();
		if(superAssembly == null) {
			performOperationInComplexAssembly((ComplexAssembly)complexAssembly);
			return 1;
		}
		
		int count = 0;
		for(Assembly siblingAssembly : superAssembly.getSubAssemblies()) {
			performOperationInComplexAssembly((ComplexAssembly)siblingAssembly);
			count++;
		}
		
		return count;
	}
	
	protected void performOperationInComplexAssembly(ComplexAssembly assembly) {
		assembly.nullOperation();
	}
	
    @Override
    public OperationId getOperationId() {
    	return OperationId.OP6;
    }
}
