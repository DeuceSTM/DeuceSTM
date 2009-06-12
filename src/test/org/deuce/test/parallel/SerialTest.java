package org.deuce.test.parallel;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.deuce.Atomic;

public class SerialTest extends TestCase {
	
	private int var0;
	private int var1;
	private int var2;
	private int var3;
	private int var4;
	
    @Override
    public void setUp() { 
    	var0 = 0;
    	var1 = 1;
    	var2 = 2;
    	var3 = 3;
    	var4 = 4;
	}
	
	public void testSingleRead() {
		atomicSingleRead();
	}
	
	@Atomic
	private void atomicSingleRead() {
		int x = var1;
		Assert.assertEquals(1, var1);
	}

	
	public void testMultiRead() {
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

	public void testSingleWrite() {
		atomicSingleWrite();
		Assert.assertEquals(10, var0);
	}
	
	@Atomic
	public void atomicSingleWrite(){
		var0 = 10;	
	}
	
	public void testMultiWrite() {
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
