package org.deuce.benchmark.bank;

/**
 * @author Pascal Felber
 * @since 0.1
 */
public class CheckingAccount extends Account {

	final private String m_name;
	private float m_balance;

	public CheckingAccount() {
		m_name = "Empty";
		m_balance = 0;
	}

	public CheckingAccount(String name) {
		m_name = name;
		m_balance = 0;
	}

	public String getName() {
		return m_name;
	}

	public float getBalance() {
		return m_balance;
	}

	public void deposit(float amount) {
		m_balance += amount;
	}

	public void withdraw(float amount) throws OverdraftException {
		if (m_balance < amount)
			throw new OverdraftException("Cannot withdraw $" + amount + " from $" + m_balance);
		m_balance -= amount;
	}
}
