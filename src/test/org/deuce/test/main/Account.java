package org.deuce.test.main;

import org.deuce.*;

/**
 * @author Pascal Felber
 * @since 0.1
 */
public class Account {

//	static volatile boolean s_disjoint = false;
//	static volatile boolean s_yield = false;

//	public String getName(){return "";}
//
//	public float getBalance(){return 1;}
//
//	public void deposit(float amount){return;}
//
//	public void withdraw(float amount) throws OverdraftException{return;}

//	@Atomic
//	static public void addInterest(Account[] accounts, float rate) {
//		for (Account a : accounts) {
////			a.deposit(a.getBalance() * rate);
//			if (s_yield)
//				Thread.yield();
//		}
//	}

	@Atomic
	static public double computeTotal() {
		double total = 0.0;
//		for (Account a : accounts) {
////			total += a.getBalance();
//			if (s_yield)
//				Thread.yield();
//		}
		return total;
	}
//
//	@Atomic
//	static public void transfer(Account src, Account dst, int amount) throws OverdraftException {
////		dst.deposit(amount);
//		if (s_yield)
//			Thread.yield();
////		src.withdraw(amount);
//	}
}
