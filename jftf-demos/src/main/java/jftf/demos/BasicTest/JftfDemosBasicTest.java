package jftf.demos.BasicTest;

import jftf.lib.tools.annotations.*;
import jftf.lib.tools.logger.JftfTestLogger;

@TestCase(featureGroup = "dev", testGroup = "examples", testVersion = "1.0")
public class JftfDemosBasicTest {
    private JftfTestLogger logger;

    @BeforeTest
    void beforeTest(){
        System.out.println("Inside BeforeTest method!");
        logger = JftfTestLogger.JftfTestLoggerFactory(this.getClass().getSimpleName());
        logger.LogInfo("Test case start!");
    }

    @Test
    void testFixture1() {
        System.out.println("Inside testFixture 1!");
        logger.LogInfo("Inside testFixture 1!");
        logger.LogWarning("Inside testFixture 1!");
        logger.LogError("Inside testFixture 1!");
    }

    @Test
    void testFixture2() {
        System.out.println("Inside testFixture 2!");
        logger.LogInfo("Inside testFixture 2!");
        logger.LogWarning("Inside testFixture 2!");
        logger.LogError("Inside testFixture 2!");
    }

    @AfterTest
    void afterTest(){
        System.out.println("Inside BeforeTest method!");
        logger.LogInfo("Test case end");
    }
}
