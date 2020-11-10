package org.deuce.benchmark.jwormbench;

import org.deuce.transform.Exclude;

import jwormbench.core.IStep;
import jwormbench.core.Direction;
import jwormbench.core.IOperation;
import jwormbench.factories.AbstractStepFactory;
import jwormbench.factories.IOperationFactory;
import jwormbench.setup.IStepSetup;

@Exclude
public class DeuceStepFactory extends AbstractStepFactory{

	public DeuceStepFactory(IStepSetup opsSetup, IOperationFactory opFac) {
		super(opsSetup, opFac);
	}

	@Override
	protected IStep factoryMethod(IOperation<?> op, Direction direction) {
		return new DeuceStep(direction, op);
	}

}
