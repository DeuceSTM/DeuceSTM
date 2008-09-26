package org.deuce.benchmark.intset;

import java.util.Random;

import org.deuce.transform.Exclude;

/**
 * @author Pascal Felber
 * @since 0.1
 */
@Exclude
public class BenchmarkThread extends org.deuce.benchmark.BenchmarkThread {
	IntSet m_set;
	int m_range;
	int m_nb_add;
	int m_nb_remove;
	int m_nb_contains;
	int m_rate;
	boolean m_write;
	int m_last;
	Random m_random;

	public BenchmarkThread(IntSet set, int range, int rate) {
		m_set = set;
		m_range = range;
		m_nb_add = m_nb_remove = m_nb_contains = 0;
		m_rate = rate;
		m_write = true;
		m_random = new Random();
	}

	protected void step() {
		int i = m_random.nextInt(100);
		if (i < m_rate) {
			if (m_write) {
				m_last = m_random.nextInt(m_range);
				if (m_set.add(m_last))
					m_write = false;
				m_nb_add++;
			} else {
				m_set.remove(m_last);
				m_nb_remove++;
				m_write = true;
			}
		} else {
			m_set.contains(m_random.nextInt(m_range));
			m_nb_contains++;
		}
	}
	
	public String stats() {
		return "A=" + m_nb_add + ", R=" + m_nb_remove + ", C=" + m_nb_contains;
	}
}
