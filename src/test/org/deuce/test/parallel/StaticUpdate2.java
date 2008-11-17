package org.deuce.test.parallel;

import org.deuce.Atomic;
import org.deuce.transaction.TransactionException;

public class StaticUpdate2 {

	private static int intVar;

    public StaticUpdate2() { 
    	intVar = 0;
	}

	public void simpleAdd() throws InterruptedException {
		int threadsCount = 2;
		final int rounds = 10000;
		Thread[] threads = new Thread[threadsCount];
		
		for( int j=0 ; j<threadsCount ; ++j)
		{
			threads[j] = new Thread(){
				@Override
				public void run(){
					for( int i=0; i<rounds ;++i)
					{
						try{
							atomicSingleUpdate();
						}
						catch( TransactionException e)
						{
							i--;
						}
					}
				}
			};
		}
		for( int j=0 ; j<threadsCount ; ++j)
		{
			threads[j].start();
		}
		for( int j=0 ; j<threadsCount ; ++j)
		{
			threads[j].join();
		}
		if (rounds*threadsCount != intVar) {
			System.out.println("Error: expected:<" + rounds*threadsCount + "> but was:<" + intVar +">");
		} else {
			System.out.println("OK");
		}
	}
	
	@Atomic
	private void atomicSingleUpdate() {
		intVar++;
	}

	public static void main(String[] args) {
		StaticUpdate2 su = new StaticUpdate2();
		try {
			su.simpleAdd();
		} catch (InterruptedException e) {
		}
	}
}
