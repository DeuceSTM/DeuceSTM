package org.deuce.test.bank;

import org.deuce.Atomic;

public class DeuceSTMFragilePaymentTransfer extends FragilePaymentTransfer {

	public DeuceSTMFragilePaymentTransfer(Account fromAccount, Account
			toAccount, int amount) {
		super(fromAccount, toAccount, amount);
	}

	@Override @Atomic public void execute() {
		super.execute();
	}
}

