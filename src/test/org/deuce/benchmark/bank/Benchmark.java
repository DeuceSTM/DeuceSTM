package org.deuce.benchmark.bank;

import org.deuce.transform.Exclude;

/**
 * @author Pascal Felber
 * @since 0.1
 */
@Exclude
public class Benchmark implements org.deuce.benchmark.Benchmark {

	int m_max = 10;
	int m_read_frequency = 0;
	int m_write_frequency = 0;
	int m_read_threads = 0;
	int m_write_threads = 0;
	Account[] m_accounts;

	public void init(String[] args) {
		int nb = 8;
		float init = 10000;
		boolean error = false;

		for (int i = 0; i < args.length && !error; i++) {
			if (args[i].equals("-n")) {
				if (++i < args.length)
					nb = Integer.parseInt(args[i]);
				else
					error = true;
			} else if (args[i].equals("-i")) {
				if (++i < args.length)
					init = Float.parseFloat(args[i]);
				else
					error = true;
			} else if (args[i].equals("-m")) {
				if (++i < args.length)
					m_max = Integer.parseInt(args[i]);
				else
					error = true;
			} else if (args[i].equals("-r")) {
				if (++i < args.length)
					m_read_frequency = Integer.parseInt(args[i]);
				else
					error = true;
			} else if (args[i].equals("-w")) {
				if (++i < args.length)
					m_write_frequency = Integer.parseInt(args[i]);
				else
					error = true;
			} else if (args[i].equals("-R")) {
				if (++i < args.length)
					m_read_threads = Integer.parseInt(args[i]);
				else
					error = true;
			} else if (args[i].equals("-W")) {
				if (++i < args.length)
					m_write_threads = Integer.parseInt(args[i]);
				else
					error = true;
			} else if (args[i].equals("-d")) {
				// Use disjoint sets of accounts
				Account.s_disjoint = true;
			} else if (args[i].equals("-y")) {
				// Can create livelocks
				Account.s_yield = true;
			} else
				error = true;
		}
		if (error) {
			System.out.println("Benchmark arguments: [-n nb-accounts] [-i initial-amount] [-m max-transfer] [-r read-all-frequency] [-w write-all-frequency] [-R read-all-threads] [-W write-all-threads] [-d] [-y]");
			System.exit(1);
		}
		m_accounts = new Account[nb];
		for (int i = 0; i < m_accounts.length; i++) {
			m_accounts[i] = new CheckingAccount("" + i);
			m_accounts[i].deposit(init);
		}
		System.out.println("Number of accounts  = " + nb);
		System.out.println("Initial amount      = " + init);
		System.out.println("Maximal transfer    = " + m_max);
		System.out.println("Read-all frequency  = " + m_read_frequency + "%");
		System.out.println("Write-all frequency = " + m_write_frequency + "%");
		System.out.println("Read-all threads    = " + m_read_threads);
		System.out.println("Write-all threads   = " + m_write_threads);
		System.out.println("Disjoint            = " + Account.s_disjoint);
		System.out.println("Yield               = " + Account.s_yield);
		System.out.println();
	}

	public void terminate() {
	}

	public org.deuce.benchmark.BenchmarkThread createThread(int i, int nb) {
		return new BenchmarkThread(i, nb, m_accounts, m_max, m_read_frequency, m_write_frequency, m_read_threads, m_write_threads);
	}

	public String getStats(org.deuce.benchmark.BenchmarkThread[] threads) {
		int total = 0;
		StringBuffer sb = new StringBuffer();
		sb.append(" [");
		for (int i = 0; i < m_accounts.length; i++) {
			total += m_accounts[i].getBalance();
			sb.append(" " + i + "=" + m_accounts[i].getBalance());
		}
		sb.append(" ]");
		int transfers = 0;
		int reads = 0;
		int writes = 0;
		for (int i = 0; i < threads.length; i++) {
			transfers += ((BenchmarkThread)threads[i]).m_nb_transfers;
			reads += ((BenchmarkThread)threads[i]).m_nb_reads;
			writes += ((BenchmarkThread)threads[i]).m_nb_writes;
		}

		return "T=" + transfers + ", R=" + reads + ", W=" + writes + ", TOTAL=" + total + sb.toString();
	}
}
