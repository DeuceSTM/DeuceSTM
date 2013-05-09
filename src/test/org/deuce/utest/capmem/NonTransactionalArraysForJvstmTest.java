package org.deuce.utest.capmem;

import org.deuce.Atomic;
import org.deuce.transform.Exclude;

import junit.framework.Assert;
import junit.framework.TestCase;

@Exclude
public class NonTransactionalArraysForJvstmTest extends TestCase{

	public void testOnarrays(){
		String s = NonTransactionalArraysTestForJvstmTarget.makeArray(new CharArrayStorage());
		Assert.assertEquals("matias", s);
	}

}


class CharArrayStorage{
	char [] elems;

	public CharArrayStorage() {
		this.elems = new char[]{'m', 'a', 't', 'i', 'a', 's'};
	}    
}


class NonTransactionalArraysTestForJvstmTarget{
	@Atomic
	public static String makeArray(){
		CharArrayStorage arr = new CharArrayStorage();
		return String.copyValueOf(arr.elems);
	}

	@Atomic
	public static String makeArray(CharArrayStorage arr){
		return String.copyValueOf(arr.elems);
	}   

}