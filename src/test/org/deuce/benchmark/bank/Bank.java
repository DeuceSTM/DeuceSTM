package org.deuce.benchmark.bank;

import java.util.*;

/**
 * @author Pascal Felber
 * @since 0.1
 */
public class Bank {

	final private List<Account> m_accounts;

	public Bank() {
		m_accounts = new LinkedList<Account>();
	}

	public Account createAccount(String name) {
		Account a = new CheckingAccount(name);
		m_accounts.add(a);
		return a;
	}
}
