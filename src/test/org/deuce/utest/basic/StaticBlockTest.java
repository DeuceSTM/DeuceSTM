package org.deuce.utest.basic;

import junit.framework.TestCase;

import org.deuce.Atomic;

/**
 * Checks that a static atomic block has no serial issues.
 *  
 * @author Guy Korland
 * @since 1.4
 */
public class StaticBlockTest extends TestCase{

	static int x = 3;
	static int y = 6;
	public void teststaticBlock() throws Exception {
		staticBlock(5);
		assertEquals(24, x);
		assertEquals(6, y);
	}
	
	@Atomic
	private static void staticBlock(int value){
		int z = 10;
		x += (value + y + z);
	}
}

