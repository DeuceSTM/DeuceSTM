package org.deuce.test.basic;

import junit.framework.TestCase;


/**
 * Test an inner private class access 
 */
public class PrivateInnerClassTest extends TestCase{

       public void testPrivateInner() throws Exception {
               new ClassA();
       }

       public static class ClassA {
               private InnerClassB privField;

               public void outerMethod() {
                       Object obj = this.privField.innerField;
               }

               private class InnerClassB {
                       Object innerField;
               }
       }
}