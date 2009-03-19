package org.deuce.test.basic;

import org.junit.Test;

/**
 * Test an inner private class access 
 */
public class PrivateInnerClassTest {

       @Test
       public void privateInner() throws Exception {
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