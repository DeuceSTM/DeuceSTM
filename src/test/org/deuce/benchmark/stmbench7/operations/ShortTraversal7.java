package org.deuce.benchmark.stmbench7.operations;

import org.deuce.benchmark.stmbench7.OperationId;
import org.deuce.benchmark.stmbench7.Setup;
import org.deuce.benchmark.stmbench7.annotations.Transactional;
import org.deuce.benchmark.stmbench7.annotations.Update;
import org.deuce.benchmark.stmbench7.core.Document;
import org.deuce.benchmark.stmbench7.core.OperationFailedException;
import org.deuce.benchmark.stmbench7.core.RuntimeError;

/**
 * Short traversal ST7 (see the specification).
 * Simple update, short.
 */
public class ShortTraversal7 extends ShortTraversal2 {

	public ShortTraversal7(Setup oo7setup) {
		super(oo7setup);
	}
	
    @Override
	@Transactional @Update
	public int performOperation() throws OperationFailedException {
    	return super.performOperation();
	}
    
    @Override
	protected int traverse(Document documentation) {
		if(documentation.textBeginsWith("I am")) return documentation.replaceText("I am", "This is");
		if(documentation.textBeginsWith("This is")) return documentation.replaceText("This is", "I am");
		
		throw new RuntimeError("ST7: unexpected beginning of Document.text!");
	}
    
    @Override
    public OperationId getOperationId() {
    	return OperationId.ST7;
    }
}
