package org.deuce.test.bank;

public interface Account {
	void deposit(int amount);
	void withdraw(int amount);
	String getAccountNumber();
	int getBalance();
}

