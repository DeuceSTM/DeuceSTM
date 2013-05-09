package org.deuce.benchmark.jwormbench;

import java.lang.reflect.Field;
import java.util.logging.Logger;

import jwormbench.core.IWorld;
import jwormbench.core.WormBench;

import org.deuce.transaction.ContextDelegator;
import org.deuce.transform.Exclude;

@Exclude
public class RunJWormBench {
	private static final String NEW_LINE = System.getProperty("line.separator");
	private static final String OPERATIONS_FILENAME_PATTERN = "org/deuce/benchmark/jwormbench/inputs/%d_ops_%d%%writes.txt";
	private static final String WORLD_FILENAME_PATTERN = "org/deuce/benchmark/jwormbench/inputs/%d.txt";
	private static final String WORMS_FILENAME_PATTERN = "org/deuce/benchmark/jwormbench/inputs/W-B[1.1]-H[%s]-%d.txt";
	private static final int worldSize = 1024;
	private static final String headSize = "2.16";
	private static final int nrOperations = 1920;

	private static String getStm(){
		try {
			Field fThCtx = ContextDelegator.class.getDeclaredField("THREAD_CONTEXT");
			fThCtx.setAccessible(true);
			Object objThLocal = fThCtx.get(null);
			Field fCtx = objThLocal.getClass().getDeclaredField("contextClass");
			fCtx.setAccessible(true);
			return fCtx.get(objThLocal).toString(); 

		} catch (NoSuchFieldException e) {throw new RuntimeException(e);
		} catch (SecurityException e) {throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {throw new RuntimeException(e);
		} catch (IllegalAccessException e) {throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws InterruptedException{
		int nrOfIterations = Integer.parseInt(args[0]);
		int nrOfThreads = Integer.parseInt(args[1]);
		int wRate = Integer.parseInt(args[2]);
		String sync = args[3];
		performTest(nrOfIterations, nrOfThreads, wRate, sync);
	}

	public static WormBench performTest(int nrOfIterations, int nrOfThreads, int wRate, String sync) throws InterruptedException{
		final String configWorms = String.format(WORMS_FILENAME_PATTERN, headSize, worldSize);
		final String configWorld= String.format(WORLD_FILENAME_PATTERN, worldSize);
		final String configOperations = String.format(OPERATIONS_FILENAME_PATTERN, nrOperations, wRate);

		//
		// Get instances
		// 
		WormBench benchRollout = null;
		Logger logger = null;
		if(sync.equals("deuce")){
			benchRollout = SyncModuleDeuce.configure(
					nrOfIterations,
					nrOfThreads,
					60, //timeout
					configWorms,
					configWorld,
					configOperations);
			logger = SyncModuleDeuce.getLogger();
		}
		else if(sync.equals("coarse")){
			benchRollout = SyncModuleCoarseLock.configure( nrOfIterations, nrOfThreads, 60, configWorms, configWorld, configOperations);
			logger = SyncModuleCoarseLock.getLogger();
		}
		else if(sync.equals("fine")){
			benchRollout = SyncModuleFineLock.configure( nrOfIterations, nrOfThreads, 60, configWorms, configWorld, configOperations);
			logger = SyncModuleFineLock.getLogger();
		}

		IWorld world = benchRollout.world;
		logger.info("-----------------------------------------------------" + NEW_LINE);
		String syncStat = getStm() + "; wrate = " + wRate;
		//
		// WarmUp 
		//
		printArguments(logger, nrOfIterations, nrOfThreads, wRate, nrOperations, syncStat, worldSize, headSize);
		logger.info("Warming up..." + NEW_LINE);
		benchRollout.RunBenchmark(syncStat, 5);
		logger.info("Warm Up Finish!" + NEW_LINE);
		logger.info("------------------------------------------------------"+ NEW_LINE);
		logger.info("------------------------------------------------------"+ NEW_LINE);
		//
		// Run 
		// 
		benchRollout.RunBenchmark(syncStat);
		benchRollout.LogExecutionTime();
		benchRollout.LogConsistencyVerification();
		//
		// Print results
		//
		return benchRollout;
	}

	private static void printArguments(
			Logger logger, 
			int nrOfIterations, 
			int nrOfThreads, 
			int wRate, 
			int nrOfOperations,
			String syncStat, 
			int worldSize, 
			String headSize)
	{
		logger.info("------------------------ ARGS ----------------" + NEW_LINE);
		String logMessage = String.format(
				"sync strategy= %s,\n" +
						"threadsNum = %d,\n" +
						"iterations = %d,\n" + 
						"world size = %d,\n" +
						"head size = %s\n" + 
						"rw trx rate = %d\n" +
						"nr of operations = %d" ,
						syncStat, nrOfThreads, nrOfIterations, worldSize, headSize, wRate, nrOfOperations);
		logger.info(logMessage + NEW_LINE);    
		logger.info("----------------------------------------------" + NEW_LINE);
	}
}