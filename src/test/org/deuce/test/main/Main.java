/*
 * @(#)Main.java   02/12/2007
 *
 * Copyright 2007 GigaSpaces Technologies Inc.
 */
package org.deuce.test.main;

import java.util.Vector;

import org.deuce.Atomic;
import org.deuce.reflection.AddressUtil;
import org.deuce.transaction.AbstractContext;
import org.deuce.transaction.global.Lock;
import org.deuce.transform.Exclude;
import org.deuce.transform.asm.StaticMethodTransformer;

/**
 * TODO add Javadoc
 *
 * @author Guy
 * @version 1.0
 * @since 5.0
 */
public class Main {

	private Vector i; 
	
	 public static final String RERAN_FAILURE = "FAILURE"; //$NON-NLS-1$
	 public static final String RERAN_ERROR = "ERROR"; //$NON-NLS-1$
	 public static final String RERAN_OK = "OK"; //$NON-NLS-1$
	 
	 private String[] fTestClassNames;
	
//	public static int xx;
//	public int y = 3;
//
//	static{
//		System.out.println("asa");
//	}

	public static void main( String[] args) {
//		new Main().f( 3l);
//		new AA().foo3( 3);
		new XX().foo(new Main());
		
//		Vector v  = f( null); 
		throw new RuntimeException("aas");
//		System.out.println("asasas");
	}
	
	@Atomic
	public static <T> T f( T t){
		return t;
	}
//	
//	static public void  x( Main m, boolean c){
//		m.i = c;
//	}
	
	public static class XX{
		public void foo( Main m){
			Vector x = m.i;
		}
	}

//
//	@Atomic
//	public long f( long x) {
//		int l= (int)x;
//		xx = l;
//		y= 1;
//		xx = y;
//		y++;
//		return y;
//	}
	

//    public static void failSame(String message)
//    {
//        String formatted = "";
//        if(message != null)
//            formatted = (new StringBuilder()).append(message).append(" ").toString();
//    }

}
