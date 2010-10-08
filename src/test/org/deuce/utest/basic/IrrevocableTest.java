package org.deuce.utest.basic;


import java.lang.reflect.Field;

import junit.framework.TestCase;

import org.deuce.Atomic;
import org.deuce.transaction.Context;
import org.deuce.transaction.ContextDelegator;

/**
 * Tests that Irrevocable is called on the context
 * 
 * @author guy
 *
 */
public class IrrevocableTest extends TestCase{

	public void testIrrevocableCalled() throws Exception {

		Context originalInstance = ContextDelegator.getInstance(); // save the real context before setting the moke
		Field declaredField = ContextDelegator.class.getDeclaredField("THREAD_CONTEXT");
		declaredField.setAccessible(true);
		ThreadLocal<Context> threadLocal = (ThreadLocal<Context>) declaredField.get(Thread.currentThread());

		try{
			MockContext context = new MockContext();
			threadLocal.set(context);

			foo();

			assertEquals(2, context.isIrrevocableCalled());
		}finally{
			threadLocal.set(originalInstance); // restore the real context
		}
	}

	@Atomic
	private void foo(){

		try{
			nativeMethod();
		}catch(UnsatisfiedLinkError e){
		}
		
		try{
			staticNativeMethod(1);
		}catch(UnsatisfiedLinkError e){
		}
		
		nonNativeMethod();
	}

	public native void nativeMethod();
	
	public native static void staticNativeMethod(int x);
	
	public void nonNativeMethod(){
		
	}


	public static class MockContext implements Context{

		private int irrevocableCalled = 0;
		
		@Override
		public void beforeReadAccess(Object obj, long field) {}

		@Override
		public boolean commit() {return true;}

		@Override
		public void init(int atomicBlockId, String metainf) {}

		@Override
		public Object onReadAccess(Object obj, Object value, long field) {return null;}

		@Override
		public boolean onReadAccess(Object obj, boolean value, long field) {return false;}

		@Override
		public byte onReadAccess(Object obj, byte value, long field) {return 0;}

		@Override
		public char onReadAccess(Object obj, char value, long field) {return 0;}

		@Override
		public short onReadAccess(Object obj, short value, long field) {return 0;}

		@Override
		public int onReadAccess(Object obj, int value, long field) {return 0;}

		@Override
		public long onReadAccess(Object obj, long value, long field) {return 0;}

		@Override
		public float onReadAccess(Object obj, float value, long field) {return 0;}

		@Override
		public double onReadAccess(Object obj, double value, long field) {return 0;}

		@Override
		public void onWriteAccess(Object obj, Object value, long field) {}

		@Override
		public void onWriteAccess(Object obj, boolean value, long field) {}

		@Override
		public void onWriteAccess(Object obj, byte value, long field) {}

		@Override
		public void onWriteAccess(Object obj, char value, long field) {}

		@Override
		public void onWriteAccess(Object obj, short value, long field) {}

		@Override
		public void onWriteAccess(Object obj, int value, long field) {}

		@Override
		public void onWriteAccess(Object obj, long value, long field) {}

		@Override
		public void onWriteAccess(Object obj, float value, long field) {}

		@Override
		public void onWriteAccess(Object obj, double value, long field) {}

		@Override
		public void rollback() {}

		@Override
		public void onIrrevocableAccess() {irrevocableCalled++;}

		public int isIrrevocableCalled() {
			return irrevocableCalled;
		}
	}
}
