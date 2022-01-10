package jftf.demos.MathTest;

import jftf.lib.tools.annotations.*;
import jftf.lib.tools.logger.JftfTestLogger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestCase(featureGroup = "dev", testGroup = "examples", testVersion = "1.0")
public class JftfDemosMathTest {
    private JftfTestLogger logger;

    static int factorial(int n){
        if (n == 0)
            return 1;
        else
            return(n * factorial(n-1));
    }

    @BeforeTest
    void beforeTest(){
        System.out.println("Inside BeforeTest method!");
        logger = JftfTestLogger.JftfTestLoggerFactory(this.getClass().getSimpleName());
        logger.LogInfo("Test case start!");
    }

    @Test
    void testFixture1() {
        logger.LogInfo("Factorial pass");
        assertEquals(factorial(3),6);
        assertEquals(factorial(4),24);
    }

    @Test
    void testFixture2() {
        System.out.println("Inside testFixture 2!");
        logger.LogInfo("Factorial fail");
        assertEquals(factorial(3),7);
        assertEquals(factorial(4),24);
    }

    @AfterTest
    void afterTest(){
        System.out.println("Inside BeforeTest method!");
        logger.LogInfo("Test case end");
    }
}
