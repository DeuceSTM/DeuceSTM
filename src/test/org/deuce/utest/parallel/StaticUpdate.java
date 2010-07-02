package org.deuce.utest.parallel;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.deuce.Atomic;
import org.deuce.transaction.TransactionException;

public class StaticUpdate extends TestCase {

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
	
	public void testSimpleAdd() throws InterruptedException {
		int threadsCount = 4;
		final int rounds = 10000;
		Thread[] threads = new Thread[threadsCount];
		
		for( int j=0 ; j<threadsCount ; ++j)
		{
			threads[j] = new Thread(){
				@Override
				public void run(){
					for( int i=0; i<rounds ;++i)
					{
						try{
							atomicSingleUpdate();
						}
						catch( TransactionException e)
						{
							i--;
						}
					}
				}
			};
		}
		for( int j=0 ; j<threadsCount ; ++j)
		{
			threads[j].start();
		}
		
		for( int j=0 ; j<threadsCount ; ++j)
		{
			threads[j].join();
		}
		
		
		Assert.assertEquals( rounds*threadsCount, intVar);
		Assert.assertEquals( rounds*threadsCount, longVar);
		Assert.assertEquals( (double)rounds*threadsCount, doubleVar);
		Assert.assertNotNull(objectVar);
		Assert.assertEquals( "a", stringVar);
		
		Assert.assertEquals( rounds*threadsCount, intArrVar[0]);
		Assert.assertEquals( rounds*threadsCount, longArrVar[0]);
		Assert.assertEquals( (double)rounds*threadsCount, doubleArrVar[0]);
		Assert.assertNotNull(objectArrvar[0]);
		Assert.assertEquals( "a", stringArrvar[0]);
		
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
	
//	@Test 
//	public void simpleAddAndDec() {
//		atomicSingleUpdateAndDec();
//		
//		Assert.assertEquals(intVar, 0);
//		Assert.assertEquals(longVar, 0);
//		Assert.assertEquals(doubleVar, 0.0);
//		Assert.assertNull(objectVar);
//		Assert.assertNull(stringVar);
//		
//		Assert.assertEquals(intArrVar[0], 0);
//		Assert.assertEquals(longArrVar[0], 0);
//		Assert.assertEquals(doubleArrVar[0], 0.0);
//		Assert.assertNull(objectArrvar[0]);
//		Assert.assertNull(stringArrvar[0]);
//		
//	}
//	
//	@Atomic
//	private void atomicSingleUpdateAndDec() {
//		intVar++;
//		intVar--;
//		
//		longVar++;
//		longVar--;
//		
//		doubleVar++;
//		doubleVar--;
//		
//		objectVar = new Object();
//		objectVar = null;
//		
//		stringVar = "a";
//		stringVar = null;
//		
//		intArrVar[0]++;
//		intArrVar[0]--;
//		
//    	longArrVar[0]++;
//    	longArrVar[0]--;
//    	
//    	doubleArrVar[0]++;
//    	doubleArrVar[0]--;
//    	
//    	objectArrvar[0] = new Object();
//    	objectArrvar[0] = null;
//    	
//    	stringArrvar[0] = new String("a");
//    	stringArrvar[0] = null;
//	}

}
