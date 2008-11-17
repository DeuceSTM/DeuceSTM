package org.deuce.test.parallel;

import junit.framework.Assert;

import org.deuce.Atomic;
import org.junit.Before;
import org.junit.Test;


public class SerialTest {
	
	private int var0;
	private int var1;
	private int var2;
	private int var3;
	private int var4;
	
    @Before 
    public void setUp() { 
    	var0 = 0;
    	var1 = 1;
    	var2 = 2;
    	var3 = 3;
    	var4 = 4;
	}
	
	@Test 
	public void SingleRead() {
		atomicSingleRead();
	}
	
	@Atomic
	private void atomicSingleRead() {
		int x = var1;
		Assert.assertEquals(1, var1);
	}

	
	@Test 
	public void MultiRead() {
		AtomicMultiRead();
	}

	@Atomic
	private void AtomicMultiRead() {
		int x = var0;
		x += var1;
		x += var2;
		x += var3;
		x += var4;
	}

	
	@Test 
	public void SingleWrite() {
		atomicSingleWrite();
		Assert.assertEquals(10, var0);
	}
	
	@Atomic
	public void atomicSingleWrite(){
		var0 = 10;	
	}
	
	@Test 
	public void MultiWrite() {
		atmicMultiWrite();
	}

	@Atomic
	private void atmicMultiWrite() {
		var0 = 10;
		var1 = 10;
		var2 = 10;
		var3 = 10;
		var4 = 10;
	}

}
