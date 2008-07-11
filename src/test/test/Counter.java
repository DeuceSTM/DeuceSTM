package test;

import org.deuce.Atomic;

public class Counter {

	int x = 0;
	
	@Atomic
	public void f(){
		++x;
	}
	
	public synchronized int get(){
		return x;
	}
	
}
