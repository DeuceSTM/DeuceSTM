/*
 * @(#)AgentTest.java   01/12/2007
 *
 * Copyright 2007 GigaSpaces Technologies Inc.
 */

package org.deuce.utest.agent;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.deuce.transaction.Context;

/**
 * Check that duplicate methods are created with the right signature. 
 *
 * @author	Guy Korland
 * @since	0.3
 */
public class AgentTest extends TestCase
{

	public void testCreateMethod()
	{
		try
		{
			assertNotNull( CCC.class.getMethod("foo", Integer.TYPE, String.class));
			assertNotNull( CCC.class.getMethod("foo", Integer.TYPE, String.class, Context.class));
		}
		catch( Exception e)
		{
			throw new RuntimeException( e);
		}
	}

	public void testCreateConstructor()
	{
		try
		{
			assertNotNull( CCC.class.getConstructor());
			assertNotNull( CCC.class.getConstructor( Context.class));
		}
		catch( Exception e)
		{
			throw new RuntimeException( e);
		}
	}

	public void testCreateMethodWithCode()
	{
		try
		{
			Method m = CCC.class.getMethod("foo", Integer.TYPE, String.class, Context.class);
			assertEquals( m.invoke(new CCC(), 4, "AAA", new org.deuce.transaction.tl2.Context()), "AAA"+4);
		}
		catch( Exception e)
		{
			throw new RuntimeException( e);
		}
	}

	/**
	 * According to the original implementation of this unit-test and to the configuration provided 
	 * by the original build.xml for the target Test, all the java RT classes were excluded from 
	 * instrumentation.
	 * This results from the default behavior of Deuce, which excludes all the JAVA rt classes, except 
	 * if the the end-user programmer explicitly specify a different Exclude system property.
	 * So, if we do not exclude the Java RT then this test will require the transactional version of 
	 * the class StringBuider, due to the string concatenation inside the foo methods.
	 * In this case, if we included the instrumented JAVA RT in the bootclasspath then this test will
	 * fail, because the string concatenation makes an internal invocation to the native method 
	 * System.arraycopy, which in turn will make the Deuce to throw a TransactionException, to try to 
	 * repeat the execution in the Irrevocable mode.   
	 * So we should exclude at least the StringBuilder class from the Deuce instrumentation, which avoids 
	 * transactional invocations thenceforward and hence to the native method araycopy.  
	 * 
	 * @author fmcarvalho
	 */
	public static class CCC
	{
		public CCC()
		{

		}

		public String foo(int x, String y)
		{
			return y + x;
		}

		public String foo2(int x, String y)
		{
			return y + x;
		}
	}
}
