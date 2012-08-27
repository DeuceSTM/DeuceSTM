package org.deuce.utest.basic;

import junit.framework.TestCase;

import org.deuce.Atomic;
import org.junit.Assert;

/**
 * FieldsInheritanceTest
 * @author guy
 *
 */
public class FieldsInheritanceTest extends TestCase{

	public void testInheritanceAccess() throws Exception {
		
		B b = new B();
		
		
		atomicMethodA(b);
		Assert.assertEquals( 2, b.a);
		
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
		b.b = 4;
	}
	
	private static class A{
		public int a;
	}
	
	private static class B extends A{
		public int b;
	}
}

