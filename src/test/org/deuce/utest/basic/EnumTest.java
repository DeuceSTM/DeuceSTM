package org.deuce.utest.basic;

import junit.framework.TestCase;

import org.deuce.Atomic;
import org.deuce.transaction.Context;

/**
 * FieldsInheritanceTest
 * @author guy
 *
 */
public class EnumTest extends TestCase{

	enum X{
		A,B;
	}
	
	X x = X.A;
	public void testInheritanceAccess() throws Exception {
		foo();
	}
	
	@Atomic
	private void foo(){
		switch(x){
		case A:
		case B:
		}
	}
}

