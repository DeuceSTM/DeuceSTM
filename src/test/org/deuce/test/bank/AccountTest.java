package org.deuce.test.bank;

import junit.framework.Assert;
import junit.framework.TestCase;

public class AccountTest extends TestCase {

	public void testTransfer(){
		Account ac1 = new AccountImpl("111");
		Account ac2 = new AccountImpl("222");
		DeuceSTMFragilePaymentTransfer transfer = new DeuceSTMFragilePaymentTransfer(ac1, ac2, 1000);
		transfer.execute();
		
		Assert.assertEquals(ac1.getBalance(), -1000);
		Assert.assertEquals(ac2.getBalance(), 1000);
	}
}
