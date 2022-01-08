package jftf.demos;

import jftf.lib.jtest.JftfRunner;

import java.lang.reflect.InvocationTargetException;

public class JftfDemosBasicTest{

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        JftfRunner.main(args,JftfDemosBasicTest.class, new JftfDemosBasicTest());
    }
}
