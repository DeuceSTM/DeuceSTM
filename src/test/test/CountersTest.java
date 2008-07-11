package test;

import java.util.Random;

public class CountersTest {

	private static final int NUMBER_OF_THREADS = 1;
	private static Counters counters = new Counters();

	public static void main (String[] args) {
		Thread[] testThreads = new Thread[NUMBER_OF_THREADS];

		for ( int i = 0; i<NUMBER_OF_THREADS; i++)
		{
			testThreads[i] = new Thread(new CountersTestThread(30, 1, 10));
			testThreads[i].start();
		}
		//wait when all threads are done
		for ( int i = 0; i<NUMBER_OF_THREADS; i++)
		{
			try {
				testThreads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		counters.test();
		System.out.println("Number of atomic failures: " + counters.failuresCounter);
	}

	private static class CountersTestThread implements Runnable {
		private int iterations;
		private int idleSleepTime;
		private int holdSleepTime;
		private long id = 0;

		public CountersTestThread (int its, int idleTime, int holdTime)
		{
			iterations = its;
			idleSleepTime = idleTime;
			holdSleepTime = holdTime;
		}

		public void run()
		{
			id = Thread.currentThread().getId();
			System.out.println(id + " started ");
			try{
				Random r = new Random();
				for( int i=0; i<iterations; i++)
				{
					Thread.sleep(idleSleepTime);		
					if( r.nextBoolean() )
					{//write
						counters.incrementFirstPair();
						counters.test();
						Thread.sleep(holdSleepTime);
						counters.test();
						counters.decrementFirstPair();
					}
					else
					{//read
						counters.incrementSecondPair();
						counters.test();
						counters.test();
						counters.decrementSecondPair();
					}
					//Thread.yield();
				}
				System.out.println(id + " done ");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}