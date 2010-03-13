package org.deuce.benchmark.stmbench7.operations;

import org.deuce.benchmark.stmbench7.OperationId;
import org.deuce.benchmark.stmbench7.Parameters;
import org.deuce.benchmark.stmbench7.Setup;
import org.deuce.benchmark.stmbench7.ThreadRandom;
import org.deuce.benchmark.stmbench7.annotations.Transactional;
import org.deuce.benchmark.stmbench7.annotations.Update;
import org.deuce.benchmark.stmbench7.backend.Index;
import org.deuce.benchmark.stmbench7.core.AssemblyBuilder;
import org.deuce.benchmark.stmbench7.core.ComplexAssembly;
import org.deuce.benchmark.stmbench7.core.Module;
import org.deuce.benchmark.stmbench7.core.OperationFailedException;

/**
 * Structural modification SM7 (see the specification).
 */
public class StructuralModification7 extends BaseOperation {

	protected AssemblyBuilder assemblyBuilder;
	protected Index<Integer,ComplexAssembly> complexAssemblyIdIndex;
	protected Module module;
	
	public StructuralModification7(Setup oo7setup) {
		this.complexAssemblyIdIndex = oo7setup.getComplexAssemblyIdIndex();
		this.module = oo7setup.getModule();
		assemblyBuilder = oo7setup.getAssemblyBuilder();
	}
	
	@Override
	@Transactional @Update
	public int performOperation() throws OperationFailedException {
		int superAssemblyId = ThreadRandom.nextInt(Parameters.MaxComplexAssemblies) + 1;
		ComplexAssembly superAssembly = complexAssemblyIdIndex.get(superAssemblyId);
		if(superAssembly == null) throw new OperationFailedException();

		assemblyBuilder.createAndRegisterAssembly(module, superAssembly);
		
		return 1;
	}

    @Override
    public OperationId getOperationId() {
    	return OperationId.SM7;
    }
}
