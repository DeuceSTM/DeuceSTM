package org.deuce.utest.capmem;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import junit.framework.Assert;

import org.deuce.Atomic;
import org.deuce.transaction.capmem.CapturedState;
import org.deuce.transaction.capmem.CapturedStateIntArray;
import org.deuce.transaction.capmem.CapturedStateObjectArray;
import org.deuce.transform.Exclude;
import org.junit.Test;

/**
 * This test must be performed with the following System properties:
 *   -javaagent:bin/deuceAgent-aom.jar
 *   -Xbootclasspath:lib/rt-deuce.jar;bin/deuceAgent.jar 
 *   -Dorg.deuce.exclude=org.deuce.utest.capmem.AvoidBarriers
 *   -Dorg.deuce.delegator=org.deuce.transaction.ContextDelegatorCapturedState
 *   
 * The ContextDelegatorCapturedState depends on a specific context filtering and 
 * an additional object metadata that is provided by a post transformation.
 * So the configuration of this delegator implicitly includes the following 
 * system properties: 
 *   -Dorg.deuce.filter=org.deuce.transaction.ContextFilterCapturedState
 *   -Dorg.deuce.transform.pos=org.deuce.transform.asm.ClassEnhancerCapturedState
 *   
 * @author mcarvalho
 */
@Exclude
public class CapMemForIntIntTest {

	@Test
	public void performTest() throws Exception{
		// Assert the top of classes hierarchy.
		Assert.assertSame(CapturedState.class, A.class.getSuperclass());

		// Assert the additional syntethic fields containing the offsets 
		Field fX = A.class.getDeclaredField("x__ADDRESS__");
		Assert.assertEquals(16, fX.getLong(null));

		// Check the modification of array types 	
		Field fElements = A.class.getDeclaredField("elements");
		Assert.assertSame(CapturedStateObjectArray.class, fElements.getType());

		// Perform a transaction in captured memory.
		A a = SimpleTrx.performTrxInCapturedMemory();
		Assert.assertEquals(4, a.x);
		Assert.assertEquals(7, a.y);
	}

	@Test(expected=UnsupportedOperationException.class)
	public void performTestOnUnwrapp() throws Exception{
		// Perform a transaction.
		A a = SimpleTrx.performTrx(new A());
	}

	@Test(expected=UnsupportedOperationException.class)
	public void performTestOnUnwrapp2() throws Exception{
		// Perform a transaction.
		int[][] arr = SimpleTrx.performTrxCreatingArray();
	}

	@Test(expected=UnsupportedOperationException.class)
	public void performTestOnUnwrapp3() throws Exception{
		// Perform a transaction.
		int[][][] arr = SimpleTrx.performTrxCreatingArray3();
	}

	private static class SimpleTrx{

		@Atomic
		public static int[][] performTrxCreatingArray(){
			return new A().elements;
		}

		@Atomic
		public static int[][][] performTrxCreatingArray3(){
			return new C().elements;
		}

		@Atomic
		public static A performTrx(A a){
			// Once the object A is not in captured memory then the following update
			// will perform a barrier and the new value stays in the write-set.
			a.x = 4;

			// The value in-place stays the same until the commit of the transaction. 
			int n = AvoidBarriers.getIntValue(a, "x");
			Assert.assertEquals(0, n);

			// the same test for the array
			a.elements[0][0] = 11;
			Object wrapper = AvoidBarriers.getObjectValue(a.elements, "elements");
			CapturedStateIntArray wrapperInts =  (CapturedStateIntArray) Array.get(wrapper, 0);
			Object elems = AvoidBarriers.getObjectValue(wrapperInts, "elements");
			Assert.assertEquals(1, Array.getInt(elems, 0));

			a.y = 7;
			return a;
		}

		@Atomic
		public static A performTrxInCapturedMemory(){
			A a = new A();
			// if the Object A is in captured memory then the following update 
			// will be in place and reading the field without an STM barrier 
			// must return the new value.
			a.x = 4;
			int n = AvoidBarriers.getIntValue(a, "x");
			Assert.assertEquals(4, n);

			// the same test for the array
			a.elements[0][0] = 11;

			// a.elements is CapturedStateObjectArray -> CapturedStateIntArray[]
			Object wrapper = AvoidBarriers.getObjectValue(a.elements, "elements");
			CapturedStateIntArray wrapperInts =  (CapturedStateIntArray) Array.get(wrapper, 0);
			Object elems = AvoidBarriers.getObjectValue(wrapperInts, "elements");
			Assert.assertEquals(11, Array.getInt(elems, 0));

			a.y = 7;
			return a;
		}
	}

	private static class A{
		public int x, y;
		public int[][] elements;


		public A() {
			this.elements = new int[3][5];
			this.elements[0][0] = 1;
		}

		@Override
		public String toString() {
			return "A [x=" + x + ", y=" + y + "]";
		}
	}

	private static class C{
		public int[][][] elements;
		public C() {
			this.elements = new int[3][5][7];
			this.elements[0][0][0] = 1;
		}

	}
}
