package org.deuce.test.basic;

import org.junit.Test;

public class InterfaceConstant {

    @Test
    public void accessConstant() {
            new Concrete();
    }

    public static interface Iface {
            public static final String CONSTANT = "value";
    }

    public static class Concrete implements Iface {}
}


