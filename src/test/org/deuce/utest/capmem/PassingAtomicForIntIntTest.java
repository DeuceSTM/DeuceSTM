package org.deuce.utest.capmem;

import org.deuce.Atomic;
import org.deuce.transform.Exclude;
import org.junit.Test;

@Exclude
public class PassingAtomicForIntIntTest {
	@Test(expected=UnsupportedOperationException.class)
	public void performTest(){
		int[][] a = Simpletrx.stepInit();
	}

	private static class Simpletrx{
		public static int[][] stepInit(){
			int[][] a = {{5,5,5,5},{3,3,3,3},{4,4,4,4},{7,7,7,7},{2,2,2,2}};
			return Simpletrx.stepAtomic(a);
		}

		@Atomic
		public static int[][] stepAtomic(int[][] arr){
			// I am not worried with performance.
			// I intentionally leave this sort with the worst algorithm.
			int [][] sorted = new int[arr.length][arr[0].length];
			int[] minVal = null; 
			int minIdx = -1;

			for (int i = 0; i < arr.length; i++) {
				for (int j = 0; j < sorted.length; j++) {
					if(minVal == null || (arr[j] != null && arr[j][0] < minVal[0])){
						minIdx = j;
						minVal = arr[j];
					}
				}
				arr[minIdx] = null;
				sorted[i] = minVal;
				minVal = null;
			}
			return sorted;
		}
	}
}

