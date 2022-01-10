package jftf.lib.core.meta;

import com.google.common.io.Files;
import jftf.core.JftfModule;
import jftf.core.ioctl.ConfigurationManager;
import jftf.core.ioctl.DatabaseDriver;
import jftf.lib.tools.annotations.TestCase;
import jftf.lib.tools.annotations.TestCaseDev;
import org.reflections.Reflections;

import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

public final class JftfMetaPackager extends JftfModule implements IJftfMetaPackager{
    private static JftfMetaPackager jftfMetaPackagerInstance = null;
    private static JftfTestCaseMetadata testCaseMetadata = null;
    private int testId = -1;
    private JftfMetaPackager(){
        JftfModule.startupSequence(ConfigurationManager.groupLoggerTestAppContextInformation);
        DatabaseDriver.DatabaseDriverFactory();
    }

    public static JftfMetaPackager JftfMetaPackagerFactory(){
        if(jftfMetaPackagerInstance == null){
            jftfMetaPackagerInstance = new JftfMetaPackager();
        }
        return jftfMetaPackagerInstance;
    }

    @Override
    public int registerTestCase(Class<?> testClasses) {
        logger.LogInfo(String.format("Registering test case '%s'",testClasses.getSimpleName()));
        if(this.lookupTestCase(this.generateTestCaseMetadata(testClasses)) == -1){
            logger.LogInfo("No entry found in the JFTF CMDB! Continuing registration...");
            databaseDriver.insertTestCase(testCaseMetadata.getTestName(),testCaseMetadata.getFeatureGroup(),testCaseMetadata.getTestGroup(),testCaseMetadata.getTestPath(),testCaseMetadata.getTestVersion());
            if(this.lookupTestCase(this.generateTestCaseMetadata(testClasses)) != -1) {
                logger.LogInfo("Test case registration complete!");
                System.out.println("Test case registration complete!");
            }
            else{
                logger.LogError("Failed to register test case!");
                System.err.println("Failed to register test case!");
                System.exit(1);
            }
        }
        else{
            logger.LogInfo(String.format("Test case '%s' entry found in the JFTF CMDB! Omitting registration!",testClasses.getSimpleName()));
            System.out.printf("Test case '%s' entry found in the JFTF CMDB! Omitting registration!%n",testClasses.getSimpleName());
            return 1;
        }
        this.updateTestCaseInformation(DatabaseDriver.modeUpdateTestCaseFirstExecution, new Timestamp(new Date().getTime()),null,Boolean.TRUE);
        return 0;
    }

    @Override
    public int lookupTestCase(JftfTestCaseMetadata jftfTestCaseMetadata) {
        if(testId == -1) {
            logger.LogInfo(String.format("Looking up test case '%s' in the JFTF CMDB", jftfTestCaseMetadata.getTestName()));
            testId = databaseDriver.lookupTestCaseMetadata(jftfTestCaseMetadata.getTestName(), jftfTestCaseMetadata.getFeatureGroup(), jftfTestCaseMetadata.getTestGroup(), jftfTestCaseMetadata.getTestPath(), jftfTestCaseMetadata.getTestVersion());
        }
        return testId;
    }

    @Override
    public JftfTestCaseMetadata generateTestCaseMetadata(Class<?> testClasses) {
        if(testCaseMetadata == null) {
            logger.LogInfo(String.format("Generating metadata for test case '%s'", testClasses.getSimpleName()));
            try {
                Reflections reflections = new Reflections(testClasses.getPackageName());
                if (reflections.getTypesAnnotatedWith(TestCase.class).size() != 0 || reflections.getTypesAnnotatedWith(TestCaseDev.class).size() !=0) {
                    Set<Class<?>> testClassReflection;
                    if(reflections.getTypesAnnotatedWith(TestCase.class).size() != 0) {
                        Boolean failedConstraint = Boolean.FALSE;
                        testClassReflection = reflections.getTypesAnnotatedWith(TestCase.class);
                        Class<?> testClass = testClassReflection.iterator().next();
                        String testName = testClass.getSimpleName();
                        String featureGroup = testClass.getAnnotation(TestCase.class).featureGroup();
                        String testGroup = testClass.getAnnotation(TestCase.class).testGroup();
                        Path testPath = Path.of(testClasses.getProtectionDomain().getCodeSource().getLocation().toURI());
                        if (!Files.getNameWithoutExtension(testPath.getFileName().toString()).equals(testName)) {
                            logger.LogError(String.format("Test name '%s' does not match executable name '%s'!", testName, Files.getNameWithoutExtension(testPath.getFileName().toString())));
                            System.err.printf("Test name '%s' does not match executable name '%s'!%n", testName, Files.getNameWithoutExtension(testPath.getFileName().toString()));
                            failedConstraint = Boolean.TRUE;
                        }
                        if (!testPath.getParent().getParent().getParent().getFileName().toString().equals(testGroup)) {
                            logger.LogError(String.format("Test group '%s' does not match test case group directory '%s'!", testGroup, testPath.getParent().getParent().getParent().getFileName()));
                            System.err.printf("Test group '%s' does not match test case group directory '%s'!%n", testGroup, testPath.getParent().getParent().getParent().getFileName());
                            failedConstraint = Boolean.TRUE;
                        }
                        if(failedConstraint == Boolean.TRUE){
                            System.exit(1);
                        }
                        String testVersion = testClass.getAnnotation(TestCase.class).testVersion();
                        logger.LogInfo("Generated metadata for test case!");
                        testCaseMetadata = new JftfTestCaseMetadata(testName, featureGroup, testGroup, testPath, testVersion);
                    }
                    else if(reflections.getTypesAnnotatedWith(TestCaseDev.class).size() != 0){
                        testClassReflection = reflections.getTypesAnnotatedWith(TestCaseDev.class);
                        Class<?> testClass = testClassReflection.iterator().next();
                        String testName = testClass.getSimpleName();
                        String featureGroup = testClass.getAnnotation(TestCaseDev.class).featureGroup();
                        String testGroup = testClass.getAnnotation(TestCaseDev.class).testGroup();
                        Path testPath = Path.of(testClasses.getProtectionDomain().getCodeSource().getLocation().toURI());
                        String testVersion = testClass.getAnnotation(TestCaseDev.class).testVersion();
                        logger.LogInfo("Generated metadata for test case!");
                        testCaseMetadata = new JftfTestCaseMetadata(testName, featureGroup, testGroup, testPath, testVersion);
                    }
                } else {
                    logger.LogError(String.format("No test class found in test case '%s'!", testClasses.getSimpleName()));
                    System.err.printf("No test class found in test case '%s'!%n", testClasses.getSimpleName());
                    System.exit(1);
                }
            } catch (Exception e) {
                logger.LogError(String.format("Failed to extract test class from test case '%s'!", testClasses.getSimpleName()));
                System.err.printf("Failed to extract test class from test case '%s'!%n", testClasses.getSimpleName());
                e.printStackTrace();
                System.exit(1);
            }
        }
        return testCaseMetadata;
    }

    @Override
    public void insertTestReportInformation(JftfTestReportInformation jftfTestReportInformation) {
        jftfTestReportInformation.setTestId(testId);
        logger.LogInfo(String.format("Inserting test report information into JFTF CMDB for test Id '%s'",jftfTestReportInformation.getTestId()));
        databaseDriver.insertTestReport(jftfTestReportInformation.getTestId(),jftfTestReportInformation.getStartupTimestamp(),jftfTestReportInformation.getEndTimestamp(),jftfTestReportInformation.getTestDuration(),jftfTestReportInformation.getErrorMessages(),jftfTestReportInformation.getLoggerOutput(), jftfTestReportInformation.getExecutionResult());
        logger.LogInfo("Test report insertion complete!");
    }

    @Override
    public void updateTestCaseInformation(String updateMode, Timestamp firstExecution, Timestamp lastExecution, Boolean executed) {
        logger.LogInfo(String.format("Updating test case information for test Id '%s'",this.testId));
        if( databaseDriver.updateTestCase(this.testId,updateMode,firstExecution,lastExecution,executed) == Boolean.TRUE){
            logger.LogInfo("Updated test case information successfully!");
        }
        else{
            logger.LogError("Failed to update test case information!");
        }
    }
}
