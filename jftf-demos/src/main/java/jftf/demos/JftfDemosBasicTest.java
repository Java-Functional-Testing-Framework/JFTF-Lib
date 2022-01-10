package jftf.demos;

import jftf.lib.tools.annotations.*;
import jftf.lib.tools.logger.JftfTestLogger;

import static org.junit.jupiter.api.Assertions.*;

@TestCaseDev(featureGroup = "dev", testGroup = "dev", testVersion = "1.0")
public class JftfDemosBasicTest {
    private String a;

    @BeforeTest
    void beforeTest(){
        System.out.println("before");
    }

    @Test
    void test1() throws InterruptedException {
        JftfTestLogger logger = JftfTestLogger.JftfTestLoggerFactory(this.getClass().getSimpleName());
        logger.LogInfo("TestLOg");
        logger.LogWarning("TestLOg");
        logger.LogError("TestLOg");
        System.out.println(logger.getOutputMessage());
        //assertTrue(false);
        System.out.println("test1");
    }

    @Test
    void test2(){
        System.out.println("test2");
    }

    @AfterTest
    void afterTest(){
        System.out.println("after");
    }
}
