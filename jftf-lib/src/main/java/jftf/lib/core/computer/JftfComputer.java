package jftf.lib.core.computer;

import jftf.core.JftfModule;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class JftfComputer extends JftfModule implements Runnable {
    protected Object testObject = null;
    protected List<Method> testFixtures = null;
    protected List<Method> beforeTest = null;
    protected List<Method> afterTest = null;
    protected List<String> errorMessages = new ArrayList<>();

    public void setTestObject(Object testObject){
        this.testObject = testObject;
    }

    public void setTestFixtures(List<Method> testFixtures){
        this.testFixtures = testFixtures;
    }

    public void setBeforeTest(List<Method> beforeTest){
        this.beforeTest = beforeTest;
    }

    public void setAfterTest(List<Method> afterTest){
        this.afterTest = afterTest;
    }

    public List<String> getErrorMessages(){
        return this.errorMessages;
    }

    protected void executeTestScenario(List<Method> testMethodList){ }
}
