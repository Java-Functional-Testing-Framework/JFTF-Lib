package jftf.lib.core.meta;

import jftf.core.JftfModule;
import jftf.core.ioctl.ConfigurationManager;
import jftf.core.ioctl.DatabaseDriver;
import jftf.lib.tools.annotations.TestCase;
import org.reflections.Reflections;

import java.nio.file.Path;
import java.util.Set;

public final class JftfMetaPackager extends JftfModule implements IJftfMetaPackager{
    private static JftfMetaPackager jftfMetaPackagerInstance = null;
    private static JftfTestCaseMetadata testCaseMetadata = null;
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
            logger.LogInfo("Test case registration complete!");
        }
        else{
            logger.LogInfo(String.format("Test case '%s' entry found in the JFTF CMDB! Omitting registration!",testClasses.getSimpleName()));
            System.out.printf("Test case '%s' entry found in the JFTF CMDB! Omitting registration!%n",testClasses.getSimpleName());
            return 1;
        }
        return 0;
    }

    @Override
    public int lookupTestCase(JftfTestCaseMetadata jftfTestCaseMetadata) {
        logger.LogInfo(String.format("Looking up test case '%s' in the JFTF CMDB",jftfTestCaseMetadata.getTestName()));
        return databaseDriver.lookupTestCaseMetadata(jftfTestCaseMetadata.getTestName(),jftfTestCaseMetadata.getFeatureGroup(),jftfTestCaseMetadata.getTestGroup(),jftfTestCaseMetadata.getTestPath(),jftfTestCaseMetadata.getTestVersion());
    }

    @Override
    public JftfTestCaseMetadata generateTestCaseMetadata(Class<?> testClasses) {
        if(testCaseMetadata == null) {
            logger.LogInfo(String.format("Generating metadata for test case '%s'", testClasses.getSimpleName()));
            try {
                Reflections reflections = new Reflections(testClasses.getPackageName());
                if (reflections.getTypesAnnotatedWith(TestCase.class).size() != 0) {
                    Set<Class<?>> testClassReflection = reflections.getTypesAnnotatedWith(TestCase.class);
                    Class<?> testClass = testClassReflection.iterator().next();
                    String testName = testClass.getSimpleName();
                    String featureGroup = testClass.getAnnotation(TestCase.class).featureGroup();
                    String testGroup = testClass.getAnnotation(TestCase.class).testGroup();
                    Path testPath = Path.of(testClasses.getProtectionDomain().getCodeSource().getLocation().toURI());
                    if (!testPath.getParent().getFileName().toString().equals(testGroup)) {
                        logger.LogError("Test group does not match test case location!");
                        System.err.printf("Test group does not match test case location!");
                        System.exit(1);
                    }
                    String testVersion = testClass.getAnnotation(TestCase.class).testVersion();
                    logger.LogInfo("Generated metadata for test case!");
                    testCaseMetadata = new JftfTestCaseMetadata(testName, featureGroup, testGroup, testPath, testVersion);
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
}
