package org.deuce.transform.util;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.deuce.transaction.tl2.BloomFilter;

/**
 * Checks the {@link BloomFilter} DS.
 * 
 * @author Guy
 * @since 0.4
 */
public class BloomFilterTest extends TestCase {

	public void testCheckInFilter(){
		BloomFilter filter = new BloomFilter();
		filter.add(34254354);
		Assert.assertTrue(filter.contains(34254354));
	}
	
	public void testCheckNotInFilter(){
		BloomFilter filter = new BloomFilter();
		filter.add(34254354);
		Assert.assertFalse(filter.contains(435646));
	}
	
	public void testCheckClearFilter(){
		BloomFilter filter = new BloomFilter();
		filter.add(34254354);
		filter.clear();
		Assert.assertFalse(filter.contains(34254354));
	}
	
	public void testCheckManyInFilter(){
		BloomFilter filter = new BloomFilter();
		for( int i=0 ; i<1000000; i+=3)
			filter.add(i);
		
		for( int i=0 ; i<1000000; i+=3)
			Assert.assertTrue(filter.contains(i));
	}
}