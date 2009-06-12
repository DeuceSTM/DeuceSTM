package org.deuce.test.basic;

import junit.framework.TestCase;

public class InterfaceConstant extends TestCase{

    public void testAccessConstant() {
            new Concrete();
    }

    public static interface Iface {
            public static final String CONSTANT = "value";
    }

    public static class Concrete implements Iface {}
}


