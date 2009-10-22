/*
 * @(#)Main.java   02/12/2007
 *
 * Copyright 2007 GigaSpaces Technologies Inc.
 */
package org.deuce.test.main;

import org.deuce.Atomic;

/**
 * @author Guy
 */
public class Main {
	
	
	public static void main(String[] args) {
		System.out.println("aasas");
		for( int i=0 ; i<1000000; ++i)
			main3(null);
		System.out.println("asdasd1212");
	}
	@Atomic
	public static void main3(String[] args) {
	
		double x = 555.0;
		B b = new B();
		b.x = (int) x;
		b.x1 = 4;
		b.y = 5;
		b.y1 = 6;
		
	}

	
	public static class A{
	
		public static int x;
		public  int x1;
		
	}
	
	public static class B extends A{
	
		public static int y;
		public int y1;
	}
	
}
