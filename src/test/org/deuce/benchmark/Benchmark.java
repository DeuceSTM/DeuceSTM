package org.deuce.benchmark;

import org.deuce.transform.Exclude;

/**
 * @author Pascal Felber
 * @since 0.1
 */
@Exclude
public interface Benchmark {

	public void init(String[] args);

	public BenchmarkThread createThread(int i, int nb);

	public String stats(BenchmarkThread[] threads);
}
