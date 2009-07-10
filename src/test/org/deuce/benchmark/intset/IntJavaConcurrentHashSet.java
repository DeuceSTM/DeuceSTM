package org.deuce.benchmark.intset;

import java.util.concurrent.ConcurrentHashMap;

public class IntJavaConcurrentHashSet implements IntSet{

	// Dummy value to associate with an Object in the backing Map
    private static final Object PRESENT = new Object();
	private final ConcurrentHashMap<Integer,Object > map = new ConcurrentHashMap<Integer,Object>();
	
	public boolean add(int value) {
		return map.put(value, PRESENT)==null;
	}

	public boolean contains(int value) {
		return map.containsKey(value);
	}

	public boolean remove(int value) {
		return map.remove(value) != null;
	}

}
