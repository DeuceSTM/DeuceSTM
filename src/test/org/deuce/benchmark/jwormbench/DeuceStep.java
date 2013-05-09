package org.deuce.benchmark.jwormbench;

import org.deuce.Atomic;

import jwormbench.core.IStep;
import jwormbench.core.Direction;
import jwormbench.core.IOperation;
import jwormbench.core.IWorm;
import jwormbench.core.OperationKind;

public class DeuceStep implements IStep{
	private final Direction direction;
	private final IOperation<?> op;

	public DeuceStep(Direction direction, IOperation<?> op) {
		super();
		this.direction = direction;
		this.op = op;
	}
	@Override
	public Object performStep(IWorm worm) {
		//
		// Perform operation
		//
		Object res = null;
		if(op.getKind().ordinal() < 5)
			res = performRoOperation(worm);
		else 
			res = performRwOperation(worm);
		//
		// Move worm
		//
		worm.move(direction);
		worm.updateWorldUnderWorm();
		return res;
	}
	@Atomic
	public Object performRwOperation(IWorm worm){
		return op.performOperation(worm);
	}
	@Atomic
	public Object performRoOperation(IWorm worm){
		return op.performOperation(worm);
	}
	/**
	 * Returns true if the world state is modified by this step.
	 * In that case the result of performStep method is an Integer. 
	 */
	public final boolean isWorldModified(){
		return op.isWorldModified();
	}
	/**
	 * The kind of Operation.
	 */
	public final OperationKind getOpKind(){
		return op.getKind();
	}
	/**
	 * Direction taken by the worm moved by this step.
	 */
	public Direction getDirection(){
		return direction;
	}
}
