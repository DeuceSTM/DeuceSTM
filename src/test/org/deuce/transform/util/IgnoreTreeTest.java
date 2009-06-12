package org.deuce.transform.util;

import junit.framework.Assert;
import junit.framework.TestCase;


public class IgnoreTreeTest extends TestCase {
	
	
	public void testCheckContainsAll(){
		IgnoreTree tree = new IgnoreTree("*");
		Assert.assertTrue( tree.contains("java/lang/String"));
	}
	
	public void testCheckContainsAllJava(){
		IgnoreTree tree = new IgnoreTree("java");
		Assert.assertTrue( tree.contains("java/lang/String"));
	}
	
	public void testCheckContainsAllJavaStar(){
		IgnoreTree tree = new IgnoreTree("java.*");
		Assert.assertTrue( tree.contains("java/lang/String"));
	}
	
	
	public void testCheckContains(){
		IgnoreTree tree = new IgnoreTree("java.lang");
		Assert.assertTrue( tree.contains("java/lang/String"));
	}
	
	public void testCheckContainsClass(){
		IgnoreTree tree = new IgnoreTree("java.lang.String");
		Assert.assertTrue( tree.contains("java/lang/String"));
	}
	
	public void testCheckContainsFewClass(){
		IgnoreTree tree = new IgnoreTree("java.lang.*");
		Assert.assertTrue( tree.contains("java/lang/String"));
		Assert.assertTrue( tree.contains("java/lang/Integer"));
	}
	
	public void testCheckContainsFewPackages(){
		IgnoreTree tree = new IgnoreTree("java.lang.*,java.util.*");
		Assert.assertTrue( tree.contains("java/lang/String"));
		Assert.assertTrue( tree.contains("java/util/HashMap"));
	}
	
	public void testCheckContainsFewPackagesAndClasses(){
		IgnoreTree tree = new IgnoreTree("java.util.*,java.lang.String");
		Assert.assertTrue( tree.contains("java/lang/String"));
		Assert.assertTrue( tree.contains("java/util/HashMap"));
	}
	
	public void testCheckContainsFewPackagesAndContainsClasses(){
		IgnoreTree tree = new IgnoreTree("java.lang.*,java.lang.String");
		Assert.assertTrue( tree.contains("java/lang/String"));
	}
	
	public void testCheckNotContains(){
		IgnoreTree tree = new IgnoreTree("java.lang");
		Assert.assertFalse( tree.contains("java/util/HashMap"));
	}
	
	public void testCheckNotContainsAll(){
		IgnoreTree tree = new IgnoreTree("java.*");
		Assert.assertFalse( tree.contains("com/sun/HashMap"));
	}
	
	public void testCheckNotContainsClass(){
		IgnoreTree tree = new IgnoreTree("java.lang.String");
		Assert.assertFalse( tree.contains("java/lang/Integer"));
	}
}
