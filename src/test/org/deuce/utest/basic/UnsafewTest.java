package org.deuce.utest.basic;

import org.deuce.Atomic;
import org.deuce.Unsafe;
import org.deuce.transaction.AbortTransactionException;
import org.deuce.transaction.TransactionException;

import junit.framework.TestCase;
import junit.framework.Assert;

public class UnsafewTest extends TestCase{

	UnsafewTestTarget target = new UnsafewTestTarget();

	public void testUnsafeWrite() {
		try{
			target.foo1();
		}catch(AbortTransactionException e){}
		Assert.assertEquals(10, target.i);
		Assert.assertEquals(0, target.x);
	}

	public void testUnsafeWithAtomic() {
		try{
			target.foo2();
		}catch(AbortTransactionException e){}
		Assert.assertEquals(20, target.i);
		Assert.assertEquals(0, target.x);
	}

	public void testRepeatUnsafe() {
		try{
			target.foo3();
		}catch(TransactionException e){}
		Assert.assertEquals(30, target.i);
		Assert.assertEquals(0, target.x);
	}

	public void testAtomicUnsafe() {
		try{
			target.foo4();
		}catch(TransactionException e){}
		Assert.assertEquals(40, target.i);
		Assert.assertEquals(0, target.x);
	}
}

/**
 * We created this auxiliary class due to the ANT restrictions.   
 * Running the build.xml with ANT then the TestCase class will 
 * be loaded before the Deuce java agent and thereby avoids 
 * the Deuce intrumentation.
 * Yet and according to the capture analysis technique we need to 
 * instrument also the TestCase base class. 
 * So, we moved the transactional part of the UnsafewTest class into 
 * UnsafewTestTarget to ensure that it does not inherit from TestCase
 * and therefore it does not have any base class that was not 
 * instrumented.  
 * 
 * @author fmcarvalho
 */
class UnsafewTestTarget{

	int i = 0;
	int x = 0;

	@Atomic
	void foo1(){
		unsafe();
		x = 10;
		throw new AbortTransactionException();
	}

	@Unsafe
	private void unsafe(){
		i = 10;
	}


	@Atomic
	void foo2(){
		unsafe2();
		x = 20;
		throw new AbortTransactionException();
	}

	@Unsafe
	private void unsafe2(){
		atomic();
		i += 10;
	}

	@Atomic
	private void atomic(){
		i = 10;
	}

	@Atomic(retries=3)
	void foo3(){
		unsafe3();
		x = 30;
		throw new TransactionException();
	}

	@Unsafe
	private void unsafe3(){
		i += 10;
	}

	@Atomic(retries=3)
	void foo4(){
		unsafe4();
		i = 40;
	}

	@Unsafe
	private void unsafe4(){
		i = 50;
	}
}
