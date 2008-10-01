///*
// * @(#)AgentTest.java   01/12/2007
// *
// * Copyright 2007 GigaSpaces Technologies Inc.
// */

package org.deuce.test.reflection;

import org.deuce.reflection.AddressUtil;
import org.deuce.transform.Exclude;
import org.junit.Assert;
import org.junit.Test;

/**
 * TODO	add Javadoc
 *
 * @author	Guy
 * @version	1.0
 * @since	5.0
 */
public class AddressUtilTest{

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

	@Test
	public void getAddress() throws NoSuchFieldException{
		long address = AddressUtil.getAddress(A.class.getField("f2"));
		Assert.assertEquals(8, address);
	}

	@Test
	public void getInheritAddress() throws NoSuchFieldException{
		long address = AddressUtil.getAddress(B.class.getField("f2"));
		Assert.assertEquals(8, address);
	}

	@Test
	public void getExtendAddress() throws NoSuchFieldException{
		long address = AddressUtil.getAddress(B.class.getField("f3"));
		Assert.assertEquals(12, address);
	}

	@Test
	public void getStaticAddress() throws NoSuchFieldException{
		long address = AddressUtil.getAddress(A.class.getField("f1"));
		Assert.assertEquals(328, address);
	}

	@Test
	public void getStaticInheritAddress() throws NoSuchFieldException{
		long address = AddressUtil.getAddress(B.class.getField("f1"));
		Assert.assertEquals(328, address);
	}

	@Test
	public void getStaticExtendAddress() throws NoSuchFieldException{
		long address = AddressUtil.getAddress(B.class.getField("f4"));
		Assert.assertEquals(328, address);
	}

	@Test
	public void failonFieldNotFound() throws NoSuchFieldException{
		try{
			AddressUtil.getAddress(A.class.getField("f3"));
			Assert.fail("Should throw NoSuchFieldException");
		}catch( NoSuchFieldException ex){
		}
	}

	@Test
	public void failonWrongAccessType() throws NoSuchFieldException{
		try{
			AddressUtil.getAddress(A.class.getField("f3"));
			Assert.fail("Should throw IllegalArgumentException");
		}catch( NoSuchFieldException ex){
		}
	}

}
