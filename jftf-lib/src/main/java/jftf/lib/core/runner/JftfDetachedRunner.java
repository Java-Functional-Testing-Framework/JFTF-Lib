package jftf.lib.core.runner;

import jftf.core.JftfModule;
import jftf.core.ioctl.ConfigurationManager;
import jftf.core.ioctl.DatabaseDriver;
import jftf.lib.core.computer.JftfComputer;
import jftf.lib.core.computer.JftfSequentialComputer;
import jftf.lib.core.meta.JftfMetaPackager;
import jftf.lib.core.meta.JftfTestReportInformation;
import jftf.lib.tools.annotations.AfterTest;
import jftf.lib.tools.annotations.BeforeTest;
import jftf.lib.tools.annotations.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class JftfDetachedRunner extends JftfRunner {
    private final Class<?> testClasses;
    private final JftfComputer jftfComputer;
    private JftfTestReportInformation testReportInformation;

    public JftfDetachedRunner(Class<?> testClasses){
        this.testClasses = testClasses;
        this.jftfComputer = new JftfSequentialComputer();
        JftfModule.startupSequence(ConfigurationManager.groupLoggerTestAppContextInformation);
        DatabaseDriver.DatabaseDriverFactory();
        JftfMetaPackager jftfMetaPackager = JftfMetaPackager.JftfMetaPackagerFactory();
        if(jftfMetaPackager.lookupTestCase(jftfMetaPackager.generateTestCaseMetadata(testClasses)) == -1){
            logger.LogError(String.format("No entry found for test case '%s' in the JFTF CMDB! Test case registration required!",this.testClasses.getSimpleName()));
            System.err.printf("No entry found for test case '%s' in the JFTF CMDB! Test case registration required!%n",this.testClasses.getSimpleName());
            System.exit(1);
        }
        logger.LogDebug("Starting JFTF detached test runner");
        logger.LogDebug(String.format("Extracting methods from test case '%s'",this.testClasses.getSimpleName()));
        this.jftfComputer.setTestFixtures(this.extractTestFixtures());
        this.jftfComputer.setBeforeTest(this.extractBeforeTest());
        this.jftfComputer.setAfterTest(this.extractAfterTest());
        logger.LogDebug("Extracted annotated methods!");
        this.jftfComputer.setTestObject(this.getTestObject());
    }

    @Override
    public void run() {
        logger.LogInfo(String.format("Starting to execute test case '%s'",this.testClasses.getSimpleName()));
        this.testReportInformation = new JftfTestReportInformation(0,new Timestamp(new Date().getTime()));
        this.jftfComputer.run();
        logger.LogInfo("Test case execution complete!");
        this.packageReport();
        this.printStatus();
        logger.LogDebug("End of JFTF detached test runner");
    }

    @Override
    protected void packageReport(){
        logger.LogDebug(String.format("Packaging test report for test case '%s'",this.testClasses.getSimpleName()));
        Timestamp endTimestamp = new Timestamp(new Date().getTime());
        this.testReportInformation.setEndTimestamp(endTimestamp);
        Time testDuration = getTimestampDiff(this.testReportInformation.getStartupTimestamp(),endTimestamp);
        this.testReportInformation.setTestDuration(testDuration);
        this.testReportInformation.setErrorMessages(this.jftfComputer.getErrorMessages());
        if(this.testReportInformation.getErrorMessages().isEmpty()){
            this.testReportInformation.setExecutionResult(JftfTestReportInformation.successfulState);
        }
        else{
            this.testReportInformation.setExecutionResult(JftfTestReportInformation.errorState);
        }
        logger.LogDebug("Test report packaging complete!");
    }

    @Override
    protected void printStatus(){
        if(this.testReportInformation.getErrorMessages().isEmpty()){
            System.out.printf("%n%n%n~~~~~~~~~~Test case '%s' succeeded!~~~~~~~~~~%n%n%n",this.testClasses.getSimpleName());
        }
        else{
            System.out.printf("%n%n%n~~~~~~~~~~Test case '%s' failed!~~~~~~~~~~%n%n%n",this.testClasses.getSimpleName());
            System.out.println(this.testReportInformation.getErrorMessages());
        }
    }

    @Override
    protected List<Method> extractTestFixtures(){
        logger.LogDebug("Extracting test fixtures from test case");
        List<Method> testFixtures = new ArrayList<>();
        for(Method method : this.testClasses.getDeclaredMethods()){
            if(method.isAnnotationPresent(Test.class)){
                try {
                    method.setAccessible(true);
                }
                catch (InaccessibleObjectException e){
                    logger.LogError("Failed to set accessibility to test method! Make sure test case module is opened to the jftf.lib module");
                    System.err.println("Failed to set accessibility to test method! Make sure test case module is opened to the jftf.lib module");
                    System.exit(1);
                }
                testFixtures.add(method);
            }
        }
        if(!testFixtures.isEmpty()){
            logger.LogDebug(String.format("Found '%s' test fixtures inside test case!",testFixtures.size()));
        }
        else{
            logger.LogError(String.format("'%s' test contains '0' test fixtures!",this.testClasses.getSimpleName()));
            System.err.printf("'%s' test contains '0' test fixtures!%n",this.testClasses.getSimpleName());
            System.exit(1);
        }
        return testFixtures;
    }

    @Override
    protected List<Method> extractBeforeTest() {
        logger.LogDebug("Extracting BeforeTest methods from test case");
        List<Method> beforeTest = new ArrayList<>();
        for(Method method : this.testClasses.getDeclaredMethods()){
            if(method.isAnnotationPresent(BeforeTest.class)){
                try {
                    method.setAccessible(true);
                }
                catch (InaccessibleObjectException e){
                    logger.LogError("Failed to set accessibility to test method! Make sure test case module is opened to the jftf.lib module");
                    System.err.println("Failed to set accessibility to test method! Make sure test case module is opened to the jftf.lib module");
                    System.exit(1);
                }
                beforeTest.add(method);
            }
        }
        if(!beforeTest.isEmpty()){
            logger.LogDebug(String.format("Found '%s' BeforeTest methods inside test case!",beforeTest.size()));
        }
        else{
            logger.LogError(String.format("'%s' test contains '0' BeforeTest methods!",this.testClasses.getSimpleName()));
            System.err.printf("'%s' test contains '0' BeforeTest methods!%n",this.testClasses.getSimpleName());
            System.exit(1);
        }
        return beforeTest;
    }

    @Override
    protected List<Method> extractAfterTest() {
        logger.LogDebug("Extracting AfterTest methods from test case");
        List<Method> afterTest = new ArrayList<>();
        for(Method method : this.testClasses.getDeclaredMethods()){
            if(method.isAnnotationPresent(AfterTest.class)){
                try {
                    method.setAccessible(true);
                }
                catch (InaccessibleObjectException e){
                    logger.LogError("Failed to set accessibility to test method! Make sure test case module is opened to the jftf.lib module");
                    System.err.println("Failed to set accessibility to test method! Make sure test case module is opened to the jftf.lib module");
                    System.exit(1);
                }
                afterTest.add(method);
            }
        }
        if(!afterTest.isEmpty()){
            logger.LogDebug(String.format("Found '%s' AfterTest methods inside test case!",afterTest.size()));
        }
        else{
            logger.LogError(String.format("'%s' test contains '0' AfterTest methods!",this.testClasses.getSimpleName()));
            System.err.printf("'%s' test contains '0' AfterTest methods!%n",this.testClasses.getSimpleName());
            System.exit(1);
        }
        return afterTest;
    }

    @Override
    protected Object getTestObject() {
        logger.LogDebug("Attempting to extract test case object");
        Object testOjbect = null;
        try {
            Constructor<?> constructor = this.testClasses.getConstructor();
            testOjbect = constructor.newInstance();
        }
        catch (NoSuchMethodException e){
            logger.LogError("Failed to retrieve test case constructor!");
            System.err.println("Failed to retrieve test case constructor!");
            e.printStackTrace();
            System.exit(1);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            logger.LogError("Failed to retrieve test case object!");
            System.err.println("Failed to retrieve test case object!");
            e.printStackTrace();
            System.exit(1);
        }
        logger.LogDebug("Successfully extracted test case object!");
        return testOjbect;
    }

    private static Time getTimestampDiff(Timestamp oldTs, Timestamp newTs) {
        long diffInMS = newTs.getTime() - oldTs.getTime();
        return new Time(TimeUnit.MILLISECONDS.convert(diffInMS, TimeUnit.MILLISECONDS));
    }
}
