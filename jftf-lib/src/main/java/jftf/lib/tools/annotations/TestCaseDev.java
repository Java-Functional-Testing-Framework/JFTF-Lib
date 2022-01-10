package jftf.lib.tools.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TestCaseDev {
    String featureGroup() default "";
    String testGroup() default "";
    String testVersion() default "";
}