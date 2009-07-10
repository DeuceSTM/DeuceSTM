package org.deuce.benchmark.intset;

import java.util.Hashtable;

public class HashTableSet implements IntSet{
	
	// Dummy value to associate with an Object in the backing Map
    private static final Object PRESENT = new Object();
	private final Hashtable<Integer,Object > map = new Hashtable<Integer,Object>();
	
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
