package jftf.demos;

import jftf.lib.tools.annotations.*;
import static org.junit.jupiter.api.Assertions.*;

public class JftfDemosBasicTest{
    private String a;

    @BeforeTest
    void beforeTest(){
        System.out.println("before");
    }

    @Test
    void test1(){
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
