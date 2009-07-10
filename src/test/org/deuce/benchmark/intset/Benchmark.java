package org.deuce.benchmark.intset;

import java.util.Random;

import org.deuce.transform.Exclude;

/**
 * @author Pascal Felber
 * @since 0.1
 */
@Exclude
public class Benchmark implements org.deuce.benchmark.Benchmark {

	IntSet m_set;
	int m_range = 1 << 16;
	int m_rate = 20;

	public void init(String[] args) {
		boolean error = false;
		int initial = 256;

		if (args.length > 0) {
			if (args[0].equals("LinkedList"))
				m_set = new IntSetLinkedList();
			else if (args[0].equals("SkipList"))
				m_set = new IntSetSkipList();
			else if (args[0].equals("IntSetHash"))
				m_set = new IntSetHash();
			else if (args[0].equals("RBTree"))
				m_set = new RBTree();
			else if (args[0].equals("IntJavaHashSet"))
				m_set = new IntJavaHashSet();
			else if (args[0].equals("IntJavaConcurrentHashSet"))
				m_set = new IntJavaConcurrentHashSet();
			else
				error = true;
		} else
			error = true;
		for (int i = 1; i < args.length && !error; i++) {
			if (args[i].equals("-i")) {
				if (++i < args.length)
					initial = Integer.parseInt(args[i]);
				else
					error = true;
			} else if (args[i].equals("-r")) {
				if (++i < args.length)
					m_range = Integer.parseInt(args[i]);
				else
					error = true;
			} else if (args[i].equals("-w")) {
				if (++i < args.length)
					m_rate = Integer.parseInt(args[i]);
				else
					error = true;
			} else
				error = true;
		}
		if (error) {
			System.out.println("Benchmark arguments: (LinkedList|SkipList|RBTree) [-i initial-size] [-r range] [-w write-rate]");
			System.exit(1);
		}
		Random random = new Random();
		for (int i = 0; i < initial; i++)
			m_set.add(random.nextInt(m_range));
		System.out.println("Initial size        = " + initial);
		System.out.println("Range               = " + m_range);
		System.out.println("Write rate          = " + m_rate + "%");
		System.out.println();
	}

	public org.deuce.benchmark.BenchmarkThread createThread(int i, int nb) {
		return new BenchmarkThread(m_set, m_range, m_rate);
	}

	public String getStats(org.deuce.benchmark.BenchmarkThread[] threads) {
		int add = 0;
		int remove = 0;
		int contains = 0;
		for (int i = 0; i < threads.length; i++) {
			add += ((BenchmarkThread)threads[i]).m_nb_add;
			remove += ((BenchmarkThread)threads[i]).m_nb_remove;
			contains += ((BenchmarkThread)threads[i]).m_nb_contains;
		}
		return "A=" + add + ", R=" + remove + ", C=" + contains;
	}
}
