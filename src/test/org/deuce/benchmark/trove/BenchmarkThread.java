package org.deuce.benchmark.trove;

import gnu.trove.TIntIntHashMap;

import java.util.Random;

import org.deuce.transform.Exclude;

/**
 * @author Guy Korland
 */
@Exclude
public class BenchmarkThread extends org.deuce.benchmark.BenchmarkThread {
	TIntIntHashMap m_set;
	int m_range;
	int m_nb_add;
	int m_nb_remove;
	int m_nb_contains;
	int m_rate;
	boolean m_write;
	int m_last;
	Random m_random;

	public BenchmarkThread(TIntIntHashMap set, int range, int rate) {
		m_set = set;
		m_range = range;
		m_nb_add = m_nb_remove = m_nb_contains = 0;
		m_rate = rate;
		m_write = true;
		m_random = new Random();
	}

	protected void step(int phase) {
		int i = m_random.nextInt(100);
		if (i < m_rate) {
			if (m_write) {
				m_last = m_random.nextInt(m_range);
				if (m_set.put(m_last, 1) == 0)
					m_write = false;
				if (phase == Benchmark.TEST_PHASE)
					m_nb_add++;
			} else {
				m_set.remove(m_last);
				if (phase == Benchmark.TEST_PHASE)
					m_nb_remove++;
				m_write = true;
			}
		} else {
			m_set.contains(m_random.nextInt(m_range));
			if (phase == Benchmark.TEST_PHASE)
				m_nb_contains++;
		}
	}
	
	public String getStats() {
		return "A=" + m_nb_add + ", R=" + m_nb_remove + ", C=" + m_nb_contains;
	}
}
