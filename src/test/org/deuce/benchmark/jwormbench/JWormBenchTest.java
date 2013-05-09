package org.deuce.benchmark.jwormbench;

import junit.framework.Assert;
import jwormbench.core.INode;
import jwormbench.core.IWorld;
import jwormbench.core.WormBench;

import org.deuce.Atomic;
import org.deuce.transform.Exclude;
import org.junit.Test;

@Exclude
public class JWormBenchTest {

	@Test
	public void testJWormBench1() throws InterruptedException{
		WormBench benchRollout = RunJWormBench.performTest(512, 4, 21, "deuce");
		assertResult(benchRollout, benchRollout.world);
	}
	@Test
	public void testJWormBench2() throws InterruptedException{
		WormBench benchRollout = RunJWormBench.performTest(512, 4, 22, "deuce");
		assertResult(benchRollout, benchRollout.world);
	}
	@Test
	public void testJWormBench3() throws InterruptedException{
		WormBench benchRollout = RunJWormBench.performTest(512, 4, 23, "deuce");
		assertResult(benchRollout, benchRollout.world);
	}
	@Test
	public void testJWormBench4() throws InterruptedException{
		WormBench benchRollout = RunJWormBench.performTest(512, 4, 51, "deuce");
		assertResult(benchRollout, benchRollout.world);
	}
	@Test
	public void testJWormBench5() throws InterruptedException{
		WormBench benchRollout = RunJWormBench.performTest(512, 4, 52, "deuce");
		assertResult(benchRollout, benchRollout.world);
	}
	@Test
	public void testJWormBench6() throws InterruptedException{
		WormBench benchRollout = RunJWormBench.performTest(512, 4, 53, "deuce");
		assertResult(benchRollout, benchRollout.world);
	}

	private static void assertResult(WormBench benchRollout, IWorld world){
		int finalWorldSum = getSumOfAllNodes(world) - benchRollout.getAccumulatedDiffOnWorld();
		Assert.assertEquals(benchRollout.initWorldSum, finalWorldSum);
	}

	/**
	 * We cannot use directly the method getSumOfAllNodes() because it will
	 * not read transactional objects using barriers and it will get inconsistent
	 * values.
	 * To force the use of barriers we must include the verification in an atomic method.  
	 */
	private static int getSumOfAllNodes(IWorld world){
		int total = 0;
		for (int i = 0; i < world.getRowsNum(); i++) {
			for (int j = 0; j < world.getColumnsNum(); j++) {
				INode node = world.getNode(i, j);
				total += getAtomicValue(node);
			}
		}
		return total;
	}
	@Atomic
	private static int getAtomicValue(INode node ){
		return node.getValue();
	}
}
