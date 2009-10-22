package org.deuce.test.main;

import org.deuce.Atomic;

/**
 * @author Pascal Felber
 * @since 0.1
 */
public class CheckingAccount{

//	private String m_name;
//	private float m_balance;
	
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

//	public CheckingAccount() {
////		m_name = "Empty";
////		m_balance = 0;
//	}

//	public CheckingAccount(String name) {
////		m_name = name;
////		m_balance = 0;
//	}

//	public String getName() {
//		return m_name;
//	}
//
//	public float getBalance() {
//		return m_balance;
//	}
//
//	public void deposit(float amount) {
//		m_balance += amount;
//	}
//
//	public void withdraw(float amount) throws OverdraftException {
//		if (m_balance < amount)
//			throw new OverdraftException("Cannot withdraw $" + amount + " from $" + m_balance);
//		m_balance -= amount;
//	}
}
