package org.deuce.benchmark.examples.intset;

import org.deuce.transform.Exclude;

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

	public BenchmarkThread(IntSet set, int range, int rate) {
		m_set = set;
		m_range = range;
		m_nb_add = m_nb_remove = m_nb_contains = 0;
		m_rate = rate;
		m_write = true;
	}

	protected void step() {
		int i = s_random.nextInt(100);
		if (i < m_rate) {
			if (m_write) {
				m_last = s_random.nextInt(m_range);
				m_set.add(m_last);
				m_nb_add++;
			} else {
				m_set.remove(m_last);
				m_nb_remove++;
			}
			m_write = !m_write;
		} else {
			m_set.contains(s_random.nextInt(m_range));
			m_nb_contains++;
		}
	}
	
	public String stats() {
		return "A=" + m_nb_add + ", R=" + m_nb_remove + ", C=" + m_nb_contains;
	}
}
