package org.deuce.utest.capmem;

import java.util.Arrays;
import java.util.logging.Logger;

import org.deuce.Atomic;
import org.deuce.transform.Exclude;
import org.junit.Test;

@Exclude
public class PassingAtomicForIntTest {

	final private static Logger logger = Logger.getLogger("org.deuce.utest.capmem");

	@Test
	public void performTest(){
		logger.info(Arrays.toString(Simpletrx.stepInit()));
	}

	private static class Simpletrx{
		public static int[] stepInit(){
			int[] a = {4,17,2,5,8,5,1,7,9,10};
			return Simpletrx.stepAtomic(a);
		}
		@Atomic
		public static int[] stepAtomic(int[] arr){
			arr[0] = 1;
			return arr;
		}
	}
}

