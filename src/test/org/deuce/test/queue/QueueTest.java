package org.deuce.test.queue;

import java.util.concurrent.atomic.AtomicInteger;

public class QueueTest {
	private static final int NUMBER_OF_THREADS = 4;
	private static final int NUMBER_OF_ATTEMPTS = 20000;
	private static final int ADD_FACTOR = 100;

	private static int numberOfElements = 10;
	private static AtomicInteger numberOfFailures = new AtomicInteger(0);
	private static PriorityQueue queue = null;

	public static void main(String[] argv){
		System.out.println("Starting...");
		parseArguments(argv);
		for(int i = 1; i <= numberOfElements; ++i){
			queue.insert(i);
		}
		MyThread threads[] = new MyThread[NUMBER_OF_THREADS];
		long start = System.nanoTime();
		for (int i = 0; i < NUMBER_OF_THREADS; i++)
			threads[i] = new MyThread();
		for (int i =0; i < NUMBER_OF_THREADS; i++)
			threads[i].start();
		try {
			for (int i =0; i < NUMBER_OF_THREADS; i++)
				threads[i].join();
		}
		catch (InterruptedException e) { };

		double successRate = 100.0 - 100.0*numberOfFailures.get()/(NUMBER_OF_THREADS*NUMBER_OF_ATTEMPTS);
		System.out.format("Finished after %d miliseconds. Success ratio %f", 
				new Long((System.nanoTime() - start) / 1000000), successRate);
	}

	private static void parseArguments(String[] argv) {
		if(argv.length != 1){
			System.out.println("Usage: java mpp.Ex3q1 <initial size of queue>");
			System.exit(0);
		}
		queue = new PriorityQueue();
		numberOfElements = Integer.parseInt(argv[0]);
	}

	private static class MyThread extends Thread
	{
		public MyThread()
		{
		}
		public void run()
		{
			long myId = Thread.currentThread().getId();
			for(int i = 0; i < NUMBER_OF_ATTEMPTS; ++i){
				//System.out.println(queue);
				int value = 0;
				boolean failureAttempt = false;
				try{
					value = queue.deleteMin();
					//System.out.println(myId + " " + value + " deleted ");
				}catch(EmptyException ex){
					System.out.println(ex);
				}catch(IllegalStateException e){
					System.out.println(e);
					failureAttempt = true;
				}
				try{
					//we try to insert anyway to try to prevent queue from emptying
					queue.insert(value + ADD_FACTOR);
					//System.out.println(myId + " " + (value + ADD_FACTOR) + " inserted ");
				}catch(IllegalStateException e){
					System.out.println(e);
					failureAttempt = true;
				}
				if(failureAttempt){
					numberOfFailures.incrementAndGet();
				}
			}
		}
	}
}
