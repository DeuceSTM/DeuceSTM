package org.deuce.test.basic;

import junit.framework.Assert;

import org.deuce.Atomic;
import org.junit.Before;
import org.junit.Test;


public class SingleStaticUpdate {

	private static int intVar;
	private static long longVar;
	private static double doubleVar;
	private static Object objectVar;
	private static String stringVar;
	
	private static int[] intArrVar;
	private static long[] longArrVar;
	private static double[] doubleArrVar;
	private static Object[] objectArrvar;
	private static String[] stringArrvar;
	private static boolean[][] booleanArrArrVar;
	
    @Before 
    public void setUp() { 
    	intVar = 0;
    	longVar = 0;
    	doubleVar = 0;
    	objectVar = null;
    	stringVar = null;
    	
    	intArrVar = new int[1];
    	longArrVar = new long[1];
    	doubleArrVar = new double[1];
    	objectArrvar = new Object[1];
    	stringArrvar = new String[1];
    	
    	booleanArrArrVar = new boolean[1][];
    	booleanArrArrVar[0] = new boolean[1];
	}
	
	@Test 
	public void simpleAdd() {
		atomicSingleUpdate();
		
		Assert.assertEquals(1, intVar);
		Assert.assertEquals(1, longVar);
		Assert.assertEquals(1.0, doubleVar);
		Assert.assertNotNull(objectVar);
		Assert.assertEquals("a", stringVar);
		
		Assert.assertEquals(1, intArrVar[0]);
		Assert.assertEquals(1, longArrVar[0]);
		Assert.assertEquals(1.0, doubleArrVar[0]);
		Assert.assertNotNull(objectArrvar[0]);
		Assert.assertEquals("a", stringArrvar[0]);
		
		Assert.assertEquals(booleanArrArrVar[0][0], true);
		
	}
	
	@Atomic
	private void atomicSingleUpdate() {
		intVar++;
		longVar++;
		doubleVar++;
		objectVar = new Object();
		stringVar = new String("a");
		
		intArrVar[0]++;
    	longArrVar[0]++;
    	doubleArrVar[0]++;
    	objectArrvar[0] = new Object();
    	stringArrvar[0] = new String("a");
    	booleanArrArrVar[0][0] = true;
	}
	
	@Test 
	public void simpleAddAndDec() {
		atomicSingleUpdateAndDec();
		
		Assert.assertEquals(intVar, 0);
		Assert.assertEquals(longVar, 0);
		Assert.assertEquals(doubleVar, 0.0);
		Assert.assertNull(objectVar);
		Assert.assertNull(stringVar);
		
		Assert.assertEquals(intArrVar[0], 0);
		Assert.assertEquals(longArrVar[0], 0);
		Assert.assertEquals(doubleArrVar[0], 0.0);
		Assert.assertNull(objectArrvar[0]);
		Assert.assertNull(stringArrvar[0]);
		
	}
	
	@Atomic
	private void atomicSingleUpdateAndDec() {
		intVar++;
		intVar--;
		
		longVar++;
		longVar--;
		
		doubleVar++;
		doubleVar--;
		
		objectVar = new Object();
		objectVar = null;
		
		stringVar = "a";
		stringVar = null;
		
		intArrVar[0]++;
		intArrVar[0]--;
		
    	longArrVar[0]++;
    	longArrVar[0]--;
    	
    	doubleArrVar[0]++;
    	doubleArrVar[0]--;
    	
    	objectArrvar[0] = new Object();
    	objectArrvar[0] = null;
    	
    	stringArrvar[0] = new String("a");
    	stringArrvar[0] = null;
	}

}
