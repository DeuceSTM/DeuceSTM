package org.deuce.test.main;

import org.deuce.Atomic;

public class AA {


    public int k;

    public static int x2;
    static{
	x2 = 5;
	System.out.println("a");
    }

    public AA(){}
//
//    public AA( Context x){
//
//    }

    public static AA getAA(){
	return null;
    }

    public void foo( Object c){
	Object c1 = c;
//	foo2(3);

    }

    public void foo2(Integer i){
	k = 3;
	int ix = k;
    }

    @Atomic( retries=3)
    public long foo3( int xx){
//	foo2(3);
//	k = 5;
	int x = k;
	x2 = 4;
	return 3;
    }
}
