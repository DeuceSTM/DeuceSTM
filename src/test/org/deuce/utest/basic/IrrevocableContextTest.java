package org.deuce.utest.basic;

import org.deuce.Atomic;
import org.deuce.Irrevocable;

import junit.framework.TestCase;

/**
 * Tests that Irrevocable transaction blocks non-Irrevocable transaction.
 * 
 * @author guy
 *
 */
public class IrrevocableContextTest extends TestCase{

	public void testIrrevocableCalled() throws Exception {
		new Thread(){
			public void run(){
				atomicCallIrrevocable();				
			}
		}.start();

		Thread.sleep(1000);
		long start = System.currentTimeMillis();
		atomic();
		long end = System.currentTimeMillis();
		assertTrue("Non-Irrevocable transaction didn't wait for Irrevocable transaction",
				end - start > 3000);
	}
	
	private int[] source = {1,2,3};
	private int[] target = {4,4,4};
	
	/** Non-Irrevocable transaction */
	@Atomic public void atomic(){
	}
	
	/** Irrevocable transaction */
	@Atomic public void atomicCallIrrevocable(){
		irrevocable();
	}
	
	@Irrevocable public void irrevocable(){
		System.arraycopy(source, 0, target, 0, 3);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
	}

}
