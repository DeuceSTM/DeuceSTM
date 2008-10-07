package org.deuce.transform.util;

import junit.framework.Assert;

import org.deuce.transaction.tl2.BloomFilter;
import org.junit.Test;

/**
 * Checks the {@link BloomFilter} DS.
 * 
 * @author Guy
 * @since 0.4
 */
public class BloomFilterTest {

	@Test
	public void checkInFilter(){
		BloomFilter filter = new BloomFilter();
		filter.add(34254354);
		Assert.assertTrue(filter.contains(34254354));
	}
	
	@Test
	public void checkNotInFilter(){
		BloomFilter filter = new BloomFilter();
		filter.add(34254354);
		Assert.assertFalse(filter.contains(435646));
	}
	
	@Test
	public void checkClearFilter(){
		BloomFilter filter = new BloomFilter();
		filter.add(34254354);
		filter.clear();
		Assert.assertFalse(filter.contains(34254354));
	}
	
	@Test
	public void checkManyInFilter(){
		BloomFilter filter = new BloomFilter();
		for( int i=0 ; i<1000000; i+=3)
			filter.add(i);
		
		for( int i=0 ; i<1000000; i+=3)
			Assert.assertTrue(filter.contains(i));
	}
}