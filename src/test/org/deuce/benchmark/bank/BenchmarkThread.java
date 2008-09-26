package org.deuce.benchmark.bank;

import java.util.Random;

import org.deuce.transform.Exclude;

/**
 * @author Pascal Felber
 * @since 0.1
 */
@Exclude
public class BenchmarkThread extends org.deuce.benchmark.BenchmarkThread {

	int m_id;
	int m_nb;
	Account[] m_accounts;
	int m_max;
	int m_read_frequency;
	int m_write_frequency;
	int m_transfers;
	Random m_random;

	BenchmarkThread(int id, int nb, Account[] accounts, int max, int read_frequency, int write_frequency) {
		m_id = id;
		m_nb = nb;
		m_accounts = accounts;
		m_max = max;
		m_read_frequency = read_frequency;
		m_write_frequency = write_frequency;
		m_transfers = 0;
		m_random = new Random();
	}

	protected void step() {
		int i = m_random.nextInt(100);

		if (i < m_read_frequency) {
			// Compute total of all accounts (read-all transaction)
			Account.computeTotal(m_accounts);
		} else if (i < m_read_frequency + m_write_frequency) {
			// Add 0% interest (write-all transaction)
			Account.addInterest(m_accounts, 0);
		} else {
			int amount = m_random.nextInt(m_max) + 1;
			Account src;
			Account dst;
			if (Account.s_disjoint && m_nb <= m_accounts.length) {
				src = m_accounts[m_random.nextInt(m_accounts.length / m_nb) * m_nb + m_id];
				dst = m_accounts[m_random.nextInt(m_accounts.length / m_nb) * m_nb + m_id];
			} else {
				src = m_accounts[m_random.nextInt(m_accounts.length)];
				dst = m_accounts[m_random.nextInt(m_accounts.length)];
			}

			try {
				Account.transfer(src, dst, amount);
				m_transfers++;
			} catch (OverdraftException e) {
				System.err.println("Overdraft: " + e.getMessage());
			}
		}
	}

	public String stats() {
		return "SUCCESS=" + m_transfers;
	}
}
