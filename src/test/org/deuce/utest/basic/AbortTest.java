package org.deuce.utest.basic;

import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.deuce.Atomic;
import org.deuce.transaction.AbortTransactionException;
import org.deuce.transaction.TransactionException;
import org.deuce.transform.Exclude;

/**
 * @author guy
 *
 */
public class AbortTest extends TestCase{
   
	/**
	 * This test requires the exclusion of the class AtomicInteger, otherwise
	 * its internal invocation to a native method will cause the re-execution 
	 * of the transaction in Irrevocable mode and misrepresent the expected number
	 * of retries.
	 */
	AbortTestTarget target = new AbortTestTarget();
	
	public void testAbort() throws Exception {
		try{
			target.foo();
			Assert.fail("Should get TransactionException");
		}catch(TransactionException ex){
			Assert.assertEquals(0, target.i);
			Assert.assertEquals(5, target.cout.get());
		}
		try{
			target.fooAbort();
			Assert.fail("Should get AbortTransactionException");
		}catch(AbortTransactionException ex){
			Assert.assertEquals(0, target.i);
			Assert.assertEquals(6, target.cout.get());
		}
	}
}

/**
 * We created this auxiliary class due to the ANT restrictions.   
 * Running the build.xml with ANT then the TestCase class will 
 * be loaded before the Deuce java agent and thereby avoids 
 * the Deuce intrumentation.
 * Yet and according to the capture analysis technique we need to 
 * instrument also the TestCase base class. 
 * So, we moved the transactional part of the AbortTest class into 
 * AbortTestTarget to ensure that it does not inherit from TestCase
 * and therefore it does not have any base class that was not 
 * instrumented.  
 * 
 * @author fmcarvalho
 */
class AbortTestTarget{
    
        final AtomicInteger cout = new AtomicInteger();
        int i = 0;
    
	@Atomic(retries=5)
	void foo(){
		++i;
		cout.incrementAndGet();
		throw new TransactionException();
	}
	
	@Atomic(retries=5)
	void fooAbort(){
		++i;
		cout.incrementAndGet();
		throw new AbortTransactionException();
	}
	
	@Exclude
	private static class Counter{
		private int count = 0;
		public void inc(){
			++count;
		}
		
		public int get(){
			return count;
		}
	}
	
}

