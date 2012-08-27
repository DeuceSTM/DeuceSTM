package org.deuce.benchmark.stmbench7.operations;

import org.deuce.benchmark.stmbench7.OperationId;
import org.deuce.benchmark.stmbench7.Setup;
import org.deuce.benchmark.stmbench7.annotations.ReadOnly;
import org.deuce.benchmark.stmbench7.annotations.Transactional;
import org.deuce.benchmark.stmbench7.backend.Index;
import org.deuce.benchmark.stmbench7.core.BaseAssembly;
import org.deuce.benchmark.stmbench7.core.CompositePart;

/**
 * Query Q5 / Short traversal ST5 (see the specification).
 * Read-only, iterate on index, short.
 */
public class Query5 extends BaseOperation {

    protected Index<Integer,BaseAssembly> baseAssemblyIdIndex;

    public Query5(Setup oo7setup) {
    	this.baseAssemblyIdIndex = oo7setup.getBaseAssemblyIdIndex();
    }
	
    @Override
    @Transactional @ReadOnly
    public int performOperation() {
    	int result = 0;

    	for(BaseAssembly assembly : baseAssemblyIdIndex) {
    		result += checkBaseAssembly(assembly);
    	}
    		
    	return result;
    }

    protected int checkBaseAssembly(BaseAssembly assembly) {
    	int assBuildDate = assembly.getBuildDate();

    	for(CompositePart part : assembly.getComponents()) {
    		if(part.getBuildDate() > assBuildDate) {
    			assembly.nullOperation();
    			return 1;
    		}
    	}

    	return 0;
    }
    
    @Override
    public OperationId getOperationId() {
    	return OperationId.ST5;
    }
}
