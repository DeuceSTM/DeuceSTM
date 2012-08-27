package org.deuce.test.nojunit;

import junit.framework.Assert;

import org.deuce.Atomic;

public class NoStaticSerial {
	
	public byte bb;
	public char cc;
	public int ss;
	public int ii;
	public long ll;
	public float ff;
	public double dd;
	public String st;
	
	public NoStaticSerial(){
		bb = 0;
		cc = 0;
		ss = 0;
		ii = 0;
		ll = 0;
		ff = 0;
		dd = 0;
	}
	
	public static void main(String[] args){
		new NoStaticSerial().testSingleRead();
		System.out.print(".");
		new NoStaticSerial().testSingleWrite();
		System.out.print(".");
		new NoStaticSerial().testMuliRead();
		System.out.print(".");
		new NoStaticSerial().testMuliWrite();
		System.out.print(".");
		new NoStaticSerial().testReadWrite();
		System.out.print(".");
	}
	

	public void testSingleRead() {
		atomicSingleRead();
		Assert.assertEquals(0, bb);
	}
	
	@Atomic
	private void atomicSingleRead() {
		int x = bb;
	}

	public void testMuliRead() {
		double result = atomicMultiRead();
		Assert.assertEquals(0d, result);
		Assert.assertEquals(0, bb);
		Assert.assertEquals(0, cc);
		Assert.assertEquals(0, ss);
		Assert.assertEquals(0, ii);
		Assert.assertEquals(0, ll);
		Assert.assertEquals(0f, ff);
		Assert.assertEquals(0d, dd);
	}

	@Atomic
	private double atomicMultiRead() {
		double x = ll;
		x += bb;
		x += cc;
		x += ss;
		x += ii;
		x += ff;
		x += dd;
		return x; 
	}

	public void testSingleWrite() {
		atomicSingleWrite();
		Assert.assertEquals(10, bb);
	}
	
	@Atomic
	public void atomicSingleWrite(){
		bb = 10;	
	}
	
	public void testMuliWrite() {
		atomicMultiWrite();
		Assert.assertEquals(10, bb);
		Assert.assertEquals(10, cc);
		Assert.assertEquals(10, ss);
		Assert.assertEquals(10, ii);
		Assert.assertEquals(10, ll);
		Assert.assertEquals(10f, ff);
		Assert.assertEquals(10d, dd);
		Assert.assertEquals("st", st);
	}

	@Atomic
	private void atomicMultiWrite() {
		bb = 10;
		cc = 10;
		ss = 10;
		ii = 10;
		ll = 10;
		ff = 10;
		dd = 10;
		st = "st";
	}
	
	public void testReadWrite() {
		atomicReadWrite();
		Assert.assertEquals(0, bb);
		Assert.assertEquals(10, cc);
		Assert.assertEquals(10, ll);
		Assert.assertEquals("10" + 10, st);
	}

	@Atomic
	private void atomicReadWrite() {
		cc = (char)bb;
		cc = 10;
		ll = cc;
		st = "10";
		st = st + ll;
	}


}
