package org.deuce.utest.capmem;

import junit.framework.Assert;

import org.deuce.transaction.capmem.CapturedStateIntArray;
import org.deuce.transaction.capmem.CapturedStateObjectArray;
import org.deuce.transaction.capmem.ContextFilterCapturedState;
import org.deuce.transform.Exclude;
import org.junit.Test;

@Exclude
public class MultiArrayTest {
	@Test
	public void performTestBiDim() throws Exception{
		int[][] elems = {{1,2,3},{4,5,6},{7,8,9}};
		CapturedStateObjectArray wrapper = new CapturedStateObjectArray(elems, new ContextFilterCapturedState(null));
		for (int i = 0; i < elems.length; i++) {
			Assert.assertSame(CapturedStateIntArray.class, wrapper.elements[i].getClass());

			// The inner array must be equals to its original
			Assert.assertSame(elems[i], ((CapturedStateIntArray)wrapper.elements[i]).elements);	    
		}
		// Now unwrap
		int[][] newElems = (int[][]) wrapper.unwrapp();
		for (int i = 0; i < elems.length; i++) {
			Assert.assertSame(elems[i], newElems[i]);	    
		}

	}

	@Test
	public void performTestBiDimObject() throws Exception{
		String[][] elems = {{"1","2","3"},{"4","5","6"},{"7","8","9"}};
		CapturedStateObjectArray wrapper = new CapturedStateObjectArray(elems, new ContextFilterCapturedState(null));
		for (int i = 0; i < elems.length; i++) {
			Assert.assertSame(CapturedStateObjectArray.class, wrapper.elements[i].getClass());

			// The inner array must be equals to its original
			Assert.assertSame(elems[i], ((CapturedStateObjectArray)wrapper.elements[i]).elements);	    
		}
		// Now unwrap
		String[][] newElems = (String[][]) wrapper.unwrapp();
		for (int i = 0; i < elems.length; i++) {
			for (int j = 0; j < elems[i].length; j++) {
				Assert.assertEquals(elems[i][j], newElems[i][j]);
			}	    
		}

	}

	@Test
	public void performTestTriDim()throws Exception{
		int[][][] elems = new int[3][7][5];
		CapturedStateObjectArray wrapper = new CapturedStateObjectArray(elems, new ContextFilterCapturedState(null));
		for (int i = 0; i < elems.length; i++) {
			// Outer wrapper is CapturedStateObjectArray
			Assert.assertSame(CapturedStateObjectArray.class, wrapper.elements[i].getClass());

			// Inner wrapper is CapturedStateIntArray
			CapturedStateObjectArray inner = (CapturedStateObjectArray) wrapper.elements[i];
			for (int j = 0; j < inner.elements.length; j++) {
				Assert.assertSame(CapturedStateIntArray.class, inner.elements[j].getClass());
				CapturedStateIntArray innerinner = (CapturedStateIntArray) inner.elements[j];
				Assert.assertSame(elems[i][j], innerinner.elements);
			}
		}

		// Now unwrap
		int[][][] newElems = (int[][][]) wrapper.unwrapp();
		for (int i = 0; i < elems.length; i++) {
			for (int j = 0; j < elems[i].length; j++) {
				Assert.assertSame(elems[i][j], newElems[i][j]);
			}	    
		}
	}

	@Test
	public void performTestTriDimObject()throws Exception{
		String[][][] elems = {
				{{"1", "4"},{"7", "10"},{"A", "d"}},
				{{"2", "5"},{"8", "11"},{"B", "e"}},
				{{"3", "6"},{"9", "12"},{"C", "f"}}};
		CapturedStateObjectArray wrapper = new CapturedStateObjectArray(elems, new ContextFilterCapturedState(null));
		for (int i = 0; i < elems.length; i++) {
			// Outer wrapper is CapturedStateObjectArray
			Assert.assertSame(CapturedStateObjectArray.class, wrapper.elements[i].getClass());

			// Inner wrapper is CapturedStateIntArray
			CapturedStateObjectArray inner = (CapturedStateObjectArray) wrapper.elements[i];
			for (int j = 0; j < inner.elements.length; j++) {
				Assert.assertSame(CapturedStateObjectArray.class, inner.elements[j].getClass());
				CapturedStateObjectArray innerinner = (CapturedStateObjectArray) inner.elements[j];
				Assert.assertSame(elems[i][j], innerinner.elements);
			}
		}
		// Now unwrap
		String[][][] newElems = (String[][][]) wrapper.unwrapp();
		for (int i = 0; i < elems.length; i++) {
			for (int j = 0; j < elems[i].length; j++) {
				for (int k = 0; k < newElems[i][j].length; k++) {
					Assert.assertEquals(elems[i][j][k], newElems[i][j][k]);   
				}
			}	    
		}

	}
}
