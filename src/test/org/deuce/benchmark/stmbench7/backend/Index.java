package org.deuce.benchmark.stmbench7.backend;

import org.deuce.benchmark.stmbench7.annotations.Atomic;
import org.deuce.benchmark.stmbench7.annotations.ReadOnly;
import org.deuce.benchmark.stmbench7.annotations.Update;

/**
 * The interface of the core part of the "backbone" of the STMBench7 benchmark --
 * indexes used by many of the benchmark operations.
 */
@Atomic
public interface Index<K extends Comparable<K>,V> extends Iterable<V> {

	@ReadOnly
    V get(K key);
	
	@Update
    void put(K key, V value);
    
	@Update
    V putIfAbsent(K key, V value);
    
	@Update
    boolean remove(K key);
    
    @ReadOnly
    public Iterable<V> getRange(K minKey, K maxKey);
    
    @ReadOnly
    public Iterable<K> getKeys();
}
