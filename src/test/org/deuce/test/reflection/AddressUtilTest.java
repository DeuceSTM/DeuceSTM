///*
// * @(#)AgentTest.java   01/12/2007
// *
// * Copyright 2007 GigaSpaces Technologies Inc.
// */

package org.deuce.test.reflection;

import junit.framework.TestCase;

import org.deuce.reflection.AddressUtil;
import org.deuce.transform.Exclude;
import org.junit.Assert;

/**
 * TODO	add Javadoc
 *
 * @author	Guy
 * @version	1.0
 * @since	5.0
 */
public class AddressUtilTest extends TestCase{
 
	@Exclude
	private static class A{
		static public int f1;
		public int f2;
	}

	@Exclude
	private static class B extends A{
		public int f3;
		static public int f4;
	}

	public void testGetAddress() throws NoSuchFieldException{
		long address = AddressUtil.getAddress(A.class.getField("f2"));
		Assert.assertEquals(8, address);
	}

	public void testGetInheritAddress() throws NoSuchFieldException{
		long address = AddressUtil.getAddress(B.class.getField("f2"));
		Assert.assertEquals(8, address);
	}

	public void testGetExtendAddress() throws NoSuchFieldException{
		long address = AddressUtil.getAddress(B.class.getField("f3"));
		Assert.assertEquals(12, address);
	}

	public void testGetStaticAddress() throws NoSuchFieldException{
		long address = AddressUtil.getAddress(A.class.getField("f1"));
		Assert.assertEquals(328, address);
	}

	public void testGetStaticInheritAddress() throws NoSuchFieldException{
		long address = AddressUtil.getAddress(B.class.getField("f1"));
		Assert.assertEquals(328, address);
	}

	public void testGetStaticExtendAddress() throws NoSuchFieldException{
		long address = AddressUtil.getAddress(B.class.getField("f4"));
		Assert.assertEquals(328, address);
	}

	public void testFailonFieldNotFound() throws NoSuchFieldException{
		try{
			AddressUtil.getAddress(A.class.getField("f3"));
			Assert.fail("Should throw NoSuchFieldException");
		}catch( NoSuchFieldException ex){
		}
	}

	public void testFailonWrongAccessType() throws NoSuchFieldException{
		try{
			AddressUtil.getAddress(A.class.getField("f3"));
			Assert.fail("Should throw IllegalArgumentException");
		}catch( NoSuchFieldException ex){
		}
	}

}
