package jftf.demos;

import jftf.lib.tools.annotations.*;
import static org.junit.jupiter.api.Assertions.*;

@TestCase(featureGroup = "dev", testGroup = "dev", testVersion = "1.0")
public class JftfDemosBasicTest {
    private String a;

    @BeforeTest
    void beforeTest(){
        System.out.println("before");
    }

    @Test
    void test1(){
        assertTrue(false);
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
