package org.deuce.utest.basic;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import org.deuce.Atomic;
import org.deuce.transaction.Context;
import org.deuce.transaction.ContextDelegator;
import org.junit.Assert;

/**
 * Tests the that different block gets different IDs.
 * 
 * @author Guy Korland
 * @since 1.4
 */
public class AtomicBlockIDTest extends TestCase{

	
	
	public void testAbort() throws Exception {
		final AtomicReference<Exception> error = new AtomicReference<Exception>();
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run(){
				Context originalInstance = ContextDelegator.getInstance(); // save the real context before setting the moke
				ThreadLocal<Context> threadLocal = null;
				try
				{
					
					Field declaredField = ContextDelegator.class.getDeclaredField("THREAD_CONTEXT");
					declaredField.setAccessible(true);
					threadLocal = (ThreadLocal<Context>) declaredField.get(Thread.currentThread());

					MockContext context = new MockContext();
					threadLocal.set(context);

					blockA();
					Assert.assertEquals("a", context.getMetainf());
					int atomicBlockIdA = context.getAtomicBlockId();

					blockB();
					Assert.assertEquals("", context.getMetainf());
					int atomicBlockIdB = context.getAtomicBlockId();
					Assert.assertFalse(atomicBlockIdA==atomicBlockIdB);

					blockC();
					Assert.assertEquals("", context.getMetainf());
					int atomicBlockIdC = context.getAtomicBlockId();
					Assert.assertFalse(atomicBlockIdA==atomicBlockIdC);
					Assert.assertFalse(atomicBlockIdB==atomicBlockIdC);
				} catch (Exception e) {
					error.equals(e);
				} 
				finally{
					if(threadLocal != null)
						threadLocal.set(originalInstance); // restore the real context
				}
			}
		});
		thread.start();
		thread.join();
		if(error.get() != null)
			throw error.get();
	}

	@Atomic(metainf="a",retries=2)
	private void blockA(){
	}

	@Atomic(retries=5)
	private void blockB(){
	}
	@Atomic()
	private void blockC(){
		blockD();
	}
	@Atomic(metainf="b")
	private void blockD(){
	}
	
	public static class MockContext implements Context{

		private int atomicBlockId;
		private String metainf;

		@Override
		public void beforeReadAccess(Object obj, long field) {
			
			
		}

		@Override
		public boolean commit() {
			
			return true;
		}

		@Override
		public void init(int atomicBlockId, String metainf) {
			this.atomicBlockId = atomicBlockId;
			this.metainf = metainf;
		}

		@Override
		public Object onReadAccess(Object obj, Object value, long field) {
			
			return null;
		}

		@Override
		public boolean onReadAccess(Object obj, boolean value, long field) {
			
			return false;
		}

		@Override
		public byte onReadAccess(Object obj, byte value, long field) {
			
			return 0;
		}

		@Override
		public char onReadAccess(Object obj, char value, long field) {
			
			return 0;
		}

		@Override
		public short onReadAccess(Object obj, short value, long field) {
			
			return 0;
		}

		@Override
		public int onReadAccess(Object obj, int value, long field) {
			
			return 0;
		}

		@Override
		public long onReadAccess(Object obj, long value, long field) {
			
			return 0;
		}

		@Override
		public float onReadAccess(Object obj, float value, long field) {
			
			return 0;
		}

		@Override
		public double onReadAccess(Object obj, double value, long field) {
			
			return 0;
		}

		@Override
		public void onWriteAccess(Object obj, Object value, long field) {
			
			
		}

		@Override
		public void onWriteAccess(Object obj, boolean value, long field) {
			
			
		}

		@Override
		public void onWriteAccess(Object obj, byte value, long field) {
			
			
		}

		@Override
		public void onWriteAccess(Object obj, char value, long field) {
			
			
		}

		@Override
		public void onWriteAccess(Object obj, short value, long field) {
			
			
		}

		@Override
		public void onWriteAccess(Object obj, int value, long field) {
			
			
		}

		@Override
		public void onWriteAccess(Object obj, long value, long field) {
			
			
		}

		@Override
		public void onWriteAccess(Object obj, float value, long field) {
			
			
		}

		@Override
		public void onWriteAccess(Object obj, double value, long field) {
			
			
		}

		@Override
		public void rollback() {
			
			
		}

		public String getMetainf() {
			return metainf;
		}

		public int getAtomicBlockId() {
			return atomicBlockId;
		}
		
		@Override
		public void onIrrevocableAccess() {
		}
		
	}
}

