/*
 * @(#)AgentTest.java   01/12/2007
 *
 * Copyright 2007 GigaSpaces Technologies Inc.
 */

package org.deuce.test.agent;

import java.lang.reflect.Method;

import org.deuce.transaction.AbstractContext;
import org.deuce.transaction.tl2.Context;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Check that duplicate methods are created with the right signutre. 
 *
 * @author	Guy Korland
 * @since	0.3
 */
public class AgentTest
{

	@Test
	public void createMethod()
	{
		try
		{
			assertNotNull( CCC.class.getMethod("foo", Integer.TYPE, String.class));
			assertNotNull( CCC.class.getMethod("foo", Integer.TYPE, String.class, AbstractContext.class));
		}
		catch( Exception e)
		{
			throw new RuntimeException( e);
		}
	}

	@Test
	public void createConstructor()
	{
		try
		{
			assertNotNull( CCC.class.getConstructor());
			assertNotNull( CCC.class.getConstructor( AbstractContext.class));
		}
		catch( Exception e)
		{
			throw new RuntimeException( e);
		}
	}

	@Test
	public void createMethodWithCode()
	{
		try
		{
			Method m = CCC.class.getMethod("foo", Integer.TYPE, String.class, AbstractContext.class);
			assertEquals( m.invoke(new CCC(), 4, "AAA", new Context()), "AAA"+4);
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
