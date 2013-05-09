package org.deuce.utest.capmem;

import org.deuce.Atomic;
import org.deuce.transform.Exclude;
import org.junit.Test;

@Exclude
public class PassingAtomicForObjectObjectTest {
	@Test(expected=UnsupportedOperationException.class)
	public void performTest(){
		Simpletrx.stepInit();
	}

	private static class Simpletrx{
		public static String[][] stepInit(){
			String[][] a = new String[0][0];
			return Simpletrx.stepAtomic(a);
		}

		@Atomic
		public static String[][] stepAtomic(String[][] arr){
			return arr;
		}
	}
}

