package org.deuce.benchmark;

import java.util.*;
import org.deuce.transform.Exclude;

@Exclude
abstract public class BenchmarkThread implements Runnable {

	volatile private boolean m_end;
	private int m_steps;
	static protected Random s_random = new Random();

	static public void initPRNG(long seed) {
		s_random = new Random(seed);
	}

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
