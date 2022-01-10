package jftf.demos.LiveExample;

import jftf.lib.tools.annotations.*;
import jftf.lib.tools.logger.JftfTestLogger;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@TestCase(featureGroup = "dev", testGroup = "production", testVersion = "1.0")
public class JftfDemosLiveExample {
    private JftfTestLogger logger;
    private List<Boolean> vc_results;

    @BeforeTest
    void beforeTest(){
        logger = JftfTestLogger.JftfTestLoggerFactory(this.getClass().getSimpleName());
        logger.LogInfo("Test case start!");
        vc_results = new ArrayList<>();
    }

    @Test
    void testFixture1() {
        logger.LogInfo("Inside testFixture 1!");
        logger.LogInfo("VC verification 1");
        this.vc_results.add(Boolean.TRUE);
    }

    @Test
    void testFixture2() {
        logger.LogInfo("Inside testFixture 2!");
        logger.LogInfo("VC verification 2");
        this.vc_results.add(Boolean.TRUE);
    }

    @Test
    void testFixture3() {
        logger.LogInfo("Inside testFixture 3!");
        logger.LogInfo("VC verification 3");
        this.vc_results.add(Boolean.FALSE);
    }

    @Test
    void testFixture4() {
        logger.LogInfo("Inside testFixture 4!");
        logger.LogInfo("VC verification 4");
        assertFalse(this.vc_results.contains(Boolean.FALSE));
    }

    @AfterTest
    void afterTest(){
        logger.LogInfo("Test case end");
    }
}
