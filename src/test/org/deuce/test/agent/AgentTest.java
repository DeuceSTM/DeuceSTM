/*
 * @(#)AgentTest.java   01/12/2007
 *
 * Copyright 2007 GigaSpaces Technologies Inc.
 */

package org.deuce.test.agent;

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
