package org.deuce.benchmark.stmbench7.operations;

import org.deuce.benchmark.stmbench7.OperationId;
import org.deuce.benchmark.stmbench7.Parameters;
import org.deuce.benchmark.stmbench7.Setup;
import org.deuce.benchmark.stmbench7.ThreadRandom;
import org.deuce.benchmark.stmbench7.annotations.Transactional;
import org.deuce.benchmark.stmbench7.annotations.Update;
import org.deuce.benchmark.stmbench7.backend.Index;
import org.deuce.benchmark.stmbench7.core.AssemblyBuilder;
import org.deuce.benchmark.stmbench7.core.BaseAssembly;
import org.deuce.benchmark.stmbench7.core.ComplexAssembly;
import org.deuce.benchmark.stmbench7.core.Module;
import org.deuce.benchmark.stmbench7.core.OperationFailedException;

/**
 * Structural modification operation SM5 (see the specification).
 */
public class StructuralModification5 extends BaseOperation {

	protected AssemblyBuilder assemblyBuilder;
	protected Index<Integer,BaseAssembly> baseAssemblyIdIndex;
	protected Module module;
	
	public StructuralModification5(Setup oo7setup) {
		this.baseAssemblyIdIndex = oo7setup.getBaseAssemblyIdIndex();
		assemblyBuilder = oo7setup.getAssemblyBuilder();
		this.module = oo7setup.getModule();
	}
	
	@Override
	@Transactional @Update
	public int performOperation() throws OperationFailedException {
		int siblingBaseAssemblyId = ThreadRandom.nextInt(Parameters.MaxBaseAssemblies) + 1;
		BaseAssembly siblingBaseAssembly = baseAssemblyIdIndex.get(siblingBaseAssemblyId);
		if(siblingBaseAssembly == null) throw new OperationFailedException();
		
		ComplexAssembly superAssembly = siblingBaseAssembly.getSuperAssembly();
		assemblyBuilder.createAndRegisterAssembly(module, superAssembly);
		
		return 0;
	}

    @Override
    public OperationId getOperationId() {
    	return OperationId.SM5;
    }
}
