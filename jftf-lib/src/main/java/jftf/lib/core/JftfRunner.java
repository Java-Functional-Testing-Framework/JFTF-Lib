package jftf.lib.core;

import jftf.core.JftfModule;

import java.lang.reflect.Method;
import java.util.List;

public abstract class JftfRunner extends JftfModule implements Runnable{

    protected List<Method> extractTestFixtures() {
        return null;
    }
    protected List<Method> extractTestFixtures(Class<?> testClasses) {
        return null;
    }
    protected List<Method> extractBeforeTest() {
        return null;
    }
    protected List<Method> extractBeforeTest(Class<?> testClasses) {
        return null;
    }
    protected List<Method> extractAfterTest() {
        return null;
    }
    protected List<Method> extractAfterTest(Class<?> testClasses) {
        return null;
    }
    protected Object getTestObject() {
        return null;
    }
    protected Object getTestObject(Class<?> testClasses) {
        return null;
    }
    protected void packageReport() {}
    protected void printStatus() {}
}
