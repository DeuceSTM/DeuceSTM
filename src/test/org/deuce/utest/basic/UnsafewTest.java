package org.deuce.utest.basic;

import org.deuce.Atomic;
import org.deuce.Unsafe;
import org.deuce.transaction.AbortTransactionException;
import org.deuce.transaction.TransactionException;

import junit.framework.TestCase;
import junit.framework.Assert;

public class UnsafewTest extends TestCase{
	
	private int i = 0;
	private int x = 0;
	
	public void testUnsafeWrite() {
		try{
			foo1();
		}catch(AbortTransactionException e){}
		Assert.assertEquals(10, i);
		Assert.assertEquals(0, x);
	}
	
	@Atomic
	private void foo1(){
		unsafe();
		x = 10;
		throw new AbortTransactionException();
	}
	
	@Unsafe
	private void unsafe(){
		i = 10;
	}
	
	public void testUnsafeWithAtomic() {
		try{
			foo2();
		}catch(AbortTransactionException e){}
		Assert.assertEquals(20, i);
		Assert.assertEquals(0, x);
	}

	@Atomic
	private void foo2(){
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
	
	public void testRepeatUnsafe() {
		try{
			foo3();
		}catch(TransactionException e){}
		Assert.assertEquals(30, i);
		Assert.assertEquals(0, x);
	}

	@Atomic(retries=3)
	private void foo3(){
		unsafe3();
		x = 30;
		throw new TransactionException();
	}
	
	@Unsafe
	private void unsafe3(){
		i += 10;
	}
	
	public void testAtomicUnsafe() {
		try{
			foo4();
		}catch(TransactionException e){}
		Assert.assertEquals(40, i);
		Assert.assertEquals(0, x);
	}

	@Atomic(retries=3)
	private void foo4(){
		unsafe4();
		i = 40;
	}
	
	@Unsafe
	private void unsafe4(){
		i = 50;
	}
}
