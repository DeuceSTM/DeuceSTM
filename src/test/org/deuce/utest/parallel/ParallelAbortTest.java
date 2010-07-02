package org.deuce.utest.parallel;

import java.util.concurrent.atomic.AtomicInteger;

import org.deuce.Atomic;
import org.deuce.transaction.AbortTransactionException;
import org.junit.Assert;

import junit.framework.TestCase;

/**
 * Test parallel threads that abort transactions
 * @author Guy Korland
 * @since 1.3
 */
public class ParallelAbortTest extends TestCase {

	final static private int THREADS = 10;
	
	public void testPrallelAbort() throws Throwable {
		
		final AtomicInteger abortCounter = new AtomicInteger();
		final Counter counter = new Counter();
		Thread[] threads = new Thread[THREADS];

		for (int i = 0; i < 10; i++) {
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < THREADS; i++) {
						try {
							counter.increment();
						} catch (InterruptedException e) {

						} catch (AbortTransactionException e) {
							abortCounter.incrementAndGet();
						}
					}
				}
			});
		}
		for (int i = 0; i < THREADS; i++) {
			threads[i].start();
		}
		for (int i = 0; i < THREADS; i++) {
			threads[i].join();
		}
		
		Assert.assertEquals(90, counter.getValue());
		Assert.assertEquals(10, abortCounter.get());
	}


	public class Counter {

		private int count = 0;

		@Atomic
		public int increment() throws InterruptedException {
			int countTemp = count;
			Thread.sleep(10);
			count = countTemp + 1;
			if (count > 90) {
				throw new AbortTransactionException();
			}
			return count;
		}

		public int getValue() {
			return count;
		}
	}

}
