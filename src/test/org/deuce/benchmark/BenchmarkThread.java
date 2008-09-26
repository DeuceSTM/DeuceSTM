package org.deuce.benchmark;

import org.deuce.transform.Exclude;

/**
 * @author Pascal Felber
 * @since 0.1
 */
@Exclude
abstract public class BenchmarkThread implements Runnable {

	volatile private boolean m_end;
	private int m_steps;

	public void end() {
		m_end = true;
	}

	public int getSteps() {
		return m_steps;
	}

	public void run() {
		m_end = false;
		m_steps = 0;
		while (!m_end) {
			step();
			m_steps++;
		}
	}

	abstract protected void step();

	abstract public String stats();
}
