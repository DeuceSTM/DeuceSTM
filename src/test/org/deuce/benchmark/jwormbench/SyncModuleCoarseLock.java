package org.deuce.benchmark.jwormbench;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.deuce.transform.Exclude;

import jwormbench.core.IWorld;
import jwormbench.core.WormBench;
import jwormbench.defaults.World;
import jwormbench.defaults.DefaultNodeFactory;
import jwormbench.defaults.DefaultCoordinateFactory;
import jwormbench.defaults.DefaultOperationFactory;
import jwormbench.defaults.DefaultWormFactory;
import jwormbench.factories.INodeFactory;
import jwormbench.factories.ICoordinateFactory;
import jwormbench.factories.IStepFactory;
import jwormbench.factories.IWormFactory;
import jwormbench.setup.WorldFileLoader;
import jwormbench.setup.IWormsSetup;
import jwormbench.setup.StepsFileLoader;
import jwormbench.setup.WormsFileLoader;
import jwormbench.sync.lock.LockStepCrossingoverFactory;

@Exclude
public class SyncModuleCoarseLock{
	@Exclude
	static class LogFormatter extends Formatter{
		public String format(LogRecord record) {
			return record.getMessage();
		}
	}


	private static Logger logger;
	public static WormBench configure(int nrOfIterations, int nrOfThreads, int timeOut,
			String configWorms, String configWorld, String configOperations) {
		//
		// World
		//
		final ICoordinateFactory cordFac =  new DefaultCoordinateFactory();
		final INodeFactory nodeFac = new DefaultNodeFactory();
		final IWorld world = new World(
				new WorldFileLoader(
						configWorld, nodeFac));
		//
		// Worms
		//
		final IWormsSetup wormSetup = new WormsFileLoader(configWorms, cordFac);
		final IWormFactory wormFac = new DefaultWormFactory(cordFac, world, wormSetup);
		//
		// Steps
		//
		final IStepFactory stepsFac = new LockStepCrossingoverFactory(
				new StepsFileLoader(configOperations), 
				new DefaultOperationFactory(world));
		logger = Logger.getLogger("");
		logger.getHandlers()[0].setFormatter(new LogFormatter());
		//
		// At the end prints number of aborted transactions
		// 
		//.... ????
		//
		// Return a new WormBench
		// 
		return new WormBench(
				world, 
				wormFac, 
				stepsFac, 
				logger, 
				nrOfThreads, 
				nrOfIterations, 
				timeOut);
	}
	public static Logger getLogger() {
		return logger;
	}
}
