package jftf.demos.PositiveTest;

import jftf.lib.tools.annotations.*;
import jftf.lib.tools.logger.JftfTestLogger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestCase(featureGroup = "dev", testGroup = "examples", testVersion = "1.0")
public class JftfDemosPositiveTest {
    private JftfTestLogger logger;
    String a = "string_same";
    String b = "string_same";

    @BeforeTest
    void beforeTest(){
        logger = JftfTestLogger.JftfTestLoggerFactory(this.getClass().getSimpleName());
        logger.LogInfo("Test case start!");
    }

    @Test
    void testFixture1() {
        logger.LogInfo("Inside testFixture 1!");
        logger.LogInfo("VC pass");
        assertEquals(a,b);
    }

    @Test
    void testFixture2() {
        logger.LogInfo("Inside testFixture 1!");
        logger.LogInfo("VC pass");
        assertTrue(true);
    }

    @AfterTest
    void afterTest(){
        logger.LogInfo("Test case end");
    }
}
