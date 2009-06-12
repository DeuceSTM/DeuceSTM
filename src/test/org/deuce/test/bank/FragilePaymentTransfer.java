package org.deuce.test.bank;

public class FragilePaymentTransfer {


	
	private final Account fromAccount;
	private final Account toAccount;
	private final int amount;

	public 	FragilePaymentTransfer(Account fromAccount, Account
			toAccount, int amount) {
				this.fromAccount = fromAccount;
				this.toAccount = toAccount;
				this.amount = amount;
	}

	public void execute(){
		fromAccount.withdraw(amount);
		toAccount.deposit(amount);
	}
	
}
