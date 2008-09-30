package org.deuce.transform.util;

import junit.framework.Assert;

import org.junit.Test;


public class IgnoreTreeTest {
	
	
	@Test
	public void checkContainsAll(){
		IgnoreTree tree = new IgnoreTree("*");
		Assert.assertTrue( tree.contains("java/lang/String"));
	}
	
	@Test
	public void checkContainsAllJava(){
		IgnoreTree tree = new IgnoreTree("java");
		Assert.assertTrue( tree.contains("java/lang/String"));
	}
	
	@Test
	public void checkContainsAllJavaStar(){
		IgnoreTree tree = new IgnoreTree("java.*");
		Assert.assertTrue( tree.contains("java/lang/String"));
	}
	
	
	@Test
	public void checkContains(){
		IgnoreTree tree = new IgnoreTree("java.lang");
		Assert.assertTrue( tree.contains("java/lang/String"));
	}
	
	@Test
	public void checkContainsClass(){
		IgnoreTree tree = new IgnoreTree("java.lang.String");
		Assert.assertTrue( tree.contains("java/lang/String"));
	}
	
	@Test
	public void checkContainsFewClass(){
		IgnoreTree tree = new IgnoreTree("java.lang.*");
		Assert.assertTrue( tree.contains("java/lang/String"));
		Assert.assertTrue( tree.contains("java/lang/Integer"));
	}
	
	@Test
	public void checkContainsFewPackages(){
		IgnoreTree tree = new IgnoreTree("java.lang.*,java.util.*");
		Assert.assertTrue( tree.contains("java/lang/String"));
		Assert.assertTrue( tree.contains("java/util/HashMap"));
	}
	
	@Test
	public void checkContainsFewPackagesAndClasses(){
		IgnoreTree tree = new IgnoreTree("java.util.*,java.lang.String");
		Assert.assertTrue( tree.contains("java/lang/String"));
		Assert.assertTrue( tree.contains("java/util/HashMap"));
	}
	
	@Test
	public void checkContainsFewPackagesAndContainsClasses(){
		IgnoreTree tree = new IgnoreTree("java.lang.*,java.lang.String");
		Assert.assertTrue( tree.contains("java/lang/String"));
	}
	
	@Test
	public void checkNotContains(){
		IgnoreTree tree = new IgnoreTree("java.lang");
		Assert.assertFalse( tree.contains("java/util/HashMap"));
	}
	
	@Test
	public void checkNotContainsAll(){
		IgnoreTree tree = new IgnoreTree("java.*");
		Assert.assertFalse( tree.contains("com/sun/HashMap"));
	}
	
	@Test
	public void checkNotContainsClass(){
		IgnoreTree tree = new IgnoreTree("java.lang.String");
		Assert.assertFalse( tree.contains("java/lang/Integer"));
	}
}
