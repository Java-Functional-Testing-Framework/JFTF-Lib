package jftf.demos.NegativeTest;

import jftf.lib.tools.annotations.*;
import jftf.lib.tools.logger.JftfTestLogger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestCase(featureGroup = "dev", testGroup = "examples", testVersion = "1.0")
public class JftfDemosNegativeTest {
    private JftfTestLogger logger;
    String a = "string_same";
    String b = "string_same";
    String c = "string_not_same";

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
        logger.LogError("VC failure");
        assertEquals(a,c);
    }

    @AfterTest
    void afterTest(){
        logger.LogInfo("Test case end");
    }
}
