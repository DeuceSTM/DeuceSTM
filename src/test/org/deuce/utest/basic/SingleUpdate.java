package org.deuce.utest.basic;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.deuce.Atomic;


public class SingleUpdate  extends TestCase{
	
	final public static boolean f = false; 

	private int intVar;
	private long longVar;
	private double doubleVar;
	private Object objectVar;
	private String stringVar;
	
	private int[] intArrVar;
	private long[] longArrVar;
	private double[] doubleArrVar;
	private Object[] objectArrvar;
	private String[] stringArrvar;
	
    @Override
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
	}
	
	public void testSimpleAdd() {
		atomicSingleUpdate();
		
		Assert.assertEquals(intVar, 1);
		Assert.assertEquals(longVar, 1);
		Assert.assertEquals(doubleVar, 1.0);
		Assert.assertNotNull(objectVar);
		Assert.assertEquals(stringVar, "a");
		
		Assert.assertEquals(intArrVar[0], 1);
		Assert.assertEquals(longArrVar[0], 1);
		Assert.assertEquals(doubleArrVar[0], 1.0);
		Assert.assertNotNull(objectArrvar[0]);
		Assert.assertEquals(stringArrvar[0], "a");
		
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
	}
	
	public void testSimpleAddAndDec() {
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
	
	public void testArrayOutofBoundAccess() {
		try{
			arrayOutofBoundAccess(-1);
			Assert.fail("Expected ArrayIndexOutOfBoundsException");
		}catch(ArrayIndexOutOfBoundsException e){}
		
		try{
			arrayOutofBoundAccess(longArrVar.length);
			Assert.fail("Expected ArrayIndexOutOfBoundsException");
		}catch(ArrayIndexOutOfBoundsException e){}
	}
	
	@Atomic
	private void arrayOutofBoundAccess(int index) {
		longArrVar[index] = 3;
	}

}
