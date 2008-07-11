package test;

import org.deuce.Atomic;

public class Counters {

	static final double MULT_FACTOR = 3.5;
	static final double ADD_FACTOR = 5;

	//counterOne == -counterTwo
	private int counterOne = 0;
	private int counterTwo = 0;

	//coutnerFour = counterThree*MULT_FACTOR + ADD_FACTOR
	private double counterThree = 0;
	private double counterFour = ADD_FACTOR;

	public int failuresCounter = 0;


	@Atomic
	public void incrementFirstPair()
	{
		counterThree++;
		counterFour = ADD_FACTOR +  counterThree * MULT_FACTOR;
	}

	@Atomic
	public void decrementFirstPair()
	{  		
		counterThree--;
		counterFour = ADD_FACTOR + counterThree * MULT_FACTOR;

	}

	@Atomic
	public void incrementSecondPair()
	{
		counterOne++;
		counterTwo = -counterOne;
	}

	@Atomic
	public void decrementSecondPair()
	{
		counterOne--;
		counterTwo = -counterOne;
	}

	@Atomic
	public void test()
	{
		if(counterOne  != -counterTwo){
			//if transaction aborts failuresCounter will return to previous value
			failuresCounter++;
		}
		if(counterFour != counterThree*MULT_FACTOR + ADD_FACTOR){
			//if transaction aborts failuresCounter will return to previous value
			failuresCounter++;
		}
	}
}
