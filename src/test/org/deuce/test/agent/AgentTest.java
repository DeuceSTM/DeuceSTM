/*
 * @(#)AgentTest.java   01/12/2007
 *
 * Copyright 2007 GigaSpaces Technologies Inc.
 */

package org.deuce.test.agent;

import java.lang.reflect.Method;

import org.deuce.transaction.tl2.Context;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * TODO	add Javadoc
 *
 * @author	Guy
 * @version	1.0
 * @since	5.0
 */
public class AgentTest
{

	@Test
	public void createMethod()
	{
		try
		{
//			MyClassLoader cl = new MyClassLoader();
//			Class c = Class.forName(className)( .getName());
			assertNotNull( CCC.class.getMethod("foo", Integer.TYPE, String.class));
			assertNotNull( CCC.class.getMethod("foo__jstm", Integer.TYPE, String.class));
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
//			MyClassLoader cl = new MyClassLoader();
//			Class c = cl.findClass( CCC.class.getName());
			assertNotNull( CCC.class.getConstructor());
			assertNotNull( CCC.class.getConstructor( Context.class));
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
//			MyClassLoader cl = new MyClassLoader();
//			Class c = cl.findClass( CCC.class.getName());
			Method m = CCC.class.getMethod("foo__jstm", new Class[] { Integer.TYPE, String.class});
			assertEquals( m.invoke(new CCC(), 4, "AAA"), "AAA"+4);
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

//	public static class MyClassLoader extends ClassLoader {
//
//		@Override
//		protected Class findClass(String name) throws ClassNotFoundException {
//			ClassReader cr;
//			try
//			{
//				cr = new ClassReader(name);
//			}
//			catch (IOException e)
//			{
//				throw new ClassNotFoundException( e.toString() ,e);
//			}
//			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
//			ClassVisitor cv = new Adapter(cw);
//			cr.accept(cv, 0);
//			byte[] b = cw.toByteArray();
//			return defineClass(name, b, 0, b.length);
//		}
//	}

}
