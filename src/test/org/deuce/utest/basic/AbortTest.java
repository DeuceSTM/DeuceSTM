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

	final private AtomicInteger cout = new AtomicInteger();
	private int i = 0;
	
	public void testAbort() throws Exception {
		try{
			foo();
			Assert.fail("Should get TransactionException");
		}catch(TransactionException ex){
			Assert.assertEquals(0, i);
			Assert.assertEquals(5, cout.get());
		}
		try{
			fooAbort();
			Assert.fail("Should get AbortTransactionException");
		}catch(AbortTransactionException ex){
			Assert.assertEquals(0, i);
			Assert.assertEquals(6, cout.get());
		}
	}
	
	@Atomic(retries=5)
	private void foo(){
		++i;
		cout.incrementAndGet();
		throw new TransactionException();
	}
	
	@Atomic(retries=5)
	private void fooAbort(){
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

