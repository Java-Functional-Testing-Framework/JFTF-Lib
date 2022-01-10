package jftf.demos.ProductionExample;

import jftf.lib.tools.annotations.*;
import jftf.lib.tools.logger.JftfTestLogger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestCase(featureGroup = "dev", testGroup = "production", testVersion = "1.0")
public class JftfDemosProductionExample {
    private JftfTestLogger logger;
    private String vc_true = "a";
    private String vc_result;

    @BeforeTest
    void beforeTest(){
        logger = JftfTestLogger.JftfTestLoggerFactory(this.getClass().getSimpleName());
        logger.LogInfo("Test case start!");
    }

    @Test
    void testFixture1() {
        logger.LogInfo("Inside testFixture 1!");
        logger.LogInfo("VC verification 1");
        this.vc_result = vc_true;
    }

    @Test
    void testFixture2() {
        logger.LogInfo("Inside testFixture 2!");
        logger.LogInfo("VC verification 2");
        assertEquals(vc_result,vc_true);
    }

    @AfterTest
    void afterTest(){
        logger.LogInfo("Test case end");
    }
}
