package org.deuce.test.basic;

import junit.framework.TestCase;

import org.deuce.Atomic;
import org.junit.Assert;

public class StaticFieldInheritanceTest extends TestCase {

	public void testInheritanceAccess() throws Exception {
		
		B b = new B();
		
		
		atomicMethodA(b);
		Assert.assertEquals( 2, b.a);
		
		atomicMethodB(b);
		Assert.assertEquals( 3, b.a);
		
		atomicMethodB(b);
		Assert.assertEquals( 3, b.a);
		Assert.assertEquals( 4, b.b);
		
	}
	
	@Atomic
	private void atomicMethodA(A a){
		a.a = 2;
	}
	
	@Atomic
	private void atomicMethodB(B b){
		b.a = 3;
	}
	
	@Atomic
	private void atomicMethodBb(B b){
		b.b = 4;
	}
	
	private static class A{
		public static int a;
	}
	
	private static class B extends A{
		public static int b;
	}
}
