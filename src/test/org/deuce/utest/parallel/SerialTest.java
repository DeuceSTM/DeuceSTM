package org.deuce.utest.parallel;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.deuce.Atomic;

public class SerialTest extends TestCase {
        SerialTestTarget target = new SerialTestTarget();
        
        @Override
        public void setUp() { 
            target.setUp();
        }
        
        public void testSingleRead() {
                target.atomicSingleRead();
        }

        public void testMultiRead() {
                target.AtomicMultiRead();
        }

        public void testSingleWrite() {
                target.atomicSingleWrite();
                Assert.assertEquals(10, target.var0);
        }
        

        public void testMultiWrite() {
                target.atmicMultiWrite();
        }
}

/**
 * We created this auxiliary class due to the ANT restrictions.   
 * Running the build.xml with ANT then the TestCase class will 
 * be loaded before the Deuce java agent and thereby avoids 
 * the Deuce intrumentation.
 * Yet and according to the capture analysis technique we need to 
 * instrument also the TestCase base class. 
 * So, we moved the transactional part of the SerialTest class into 
 * SerialTestTarget to ensure that it does not inherit from TestCase
 * and therefore it does not have any base class that was not 
 * instrumented.  
 * 
 * @author fmcarvalho
 */
class SerialTestTarget{
    
	int var0;
	private int var1;
	private int var2;
	private int var3;
	private int var4;
	
    
        public void setUp() { 
        	var0 = 0;
        	var1 = 1;
        	var2 = 2;
        	var3 = 3;
        	var4 = 4;
	}
		
	@Atomic
	void atomicSingleRead() {
		int x = var1;
		Assert.assertEquals(1, var1);
	}

	
	@Atomic
	void AtomicMultiRead() {
		int x = var0;
		x += var1;
		x += var2;
		x += var3;
		x += var4;
	}

	@Atomic
	void atomicSingleWrite(){
		var0 = 10;	
	}
	
	@Atomic
	void atmicMultiWrite() {
		var0 = 10;
		var1 = 10;
		var2 = 10;
		var3 = 10;
		var4 = 10;
	}

}
