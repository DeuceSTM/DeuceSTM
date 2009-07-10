package org.deuce.benchmark.intset;

import org.deuce.benchmark.java.util.HashSet;

public class IntJavaHashSet implements IntSet{
	
	private final HashSet<Integer> set = new HashSet<Integer>();
	
	public boolean add(int value) {
		return set.add(value);
	}

	public boolean contains(int value) {
		return set.contains(value);
	}

	public boolean remove(int value) {
		return set.remove(value);
	}

}
