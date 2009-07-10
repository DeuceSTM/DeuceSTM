/*
 * @(#)Main.java   02/12/2007
 *
 * Copyright 2007 GigaSpaces Technologies Inc.
 */
package org.deuce.test.main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.deuce.Atomic;
import org.deuce.reflection.AddressUtil;
import org.deuce.transaction.TransactionException;
import org.deuce.transaction.global.Lock;
import org.deuce.transform.Exclude;

import test.Main.AAA;

/**
 * TODO add Javadoc
 *
 * @author Guy
 * @version 1.0
 * @since 5.0
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
