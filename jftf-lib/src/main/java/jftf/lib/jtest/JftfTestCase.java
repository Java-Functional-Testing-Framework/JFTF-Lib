package jftf.lib.jtest;

import jftf.core.JftfModule;

import java.util.concurrent.Callable;


public class JftfTestCase extends JftfModule implements IJftfTestCase, Callable<Object> {

    @Override
    public Object call() throws Exception {
        return null;
    }
}
