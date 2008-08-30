package org.deuce.benchmark;

import org.deuce.transform.Exclude;

@Exclude
public interface Benchmark {

	public void init(String[] args);

	public BenchmarkThread createThread(int i);

	public String stats(BenchmarkThread[] threads);
}
