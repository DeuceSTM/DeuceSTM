package org.deuce.test.basic;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.deuce.Atomic;
import org.deuce.transaction.TransactionException;

/**
 * @author guy
 *
 */
public class AbortTest extends TestCase{

	private int i = 0;
	
	public void testAbort() throws Exception {
		try{
			foo();
			Assert.fail("Should get TransactionException");
		}catch(TransactionException ex){
			Assert.assertEquals(0, i);
		}
	}
	
	@Atomic(retries=5)
	private void foo(){
		++i;
		throw new TransactionException();
	}
}

