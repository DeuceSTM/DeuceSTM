package org.deuce.benchmark.bank;

import org.deuce.*;

/**
 * @author Pascal Felber
 * @since 0.1
 */
abstract public class Account {

	static volatile boolean s_disjoint = false;
	static volatile boolean s_yield = false;

	abstract public String getName();

	abstract public float getBalance();

	abstract public void deposit(float amount);

	abstract public void withdraw(float amount) throws OverdraftException;

	@Atomic
	static public void addInterest(Account[] accounts, float rate) {
		for (Account a : accounts) {
			a.deposit(a.getBalance() * rate);
			if (s_yield)
				Thread.yield();
		}
	}

	@Atomic
	static public double computeTotal(Account[] accounts) {
		double total = 0.0;
		for (Account a : accounts) {
			total += a.getBalance();
			if (s_yield)
				Thread.yield();
		}
		return total;
	}

	@Atomic
	static public void transfer(Account src, Account dst, float amount) throws OverdraftException {
		dst.deposit(amount);
		if (s_yield)
			Thread.yield();
		src.withdraw(amount);
	}
}
