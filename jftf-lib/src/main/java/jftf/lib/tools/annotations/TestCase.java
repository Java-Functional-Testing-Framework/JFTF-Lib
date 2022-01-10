package jftf.lib.tools.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TestCase {
    String featureGroup() default "";
    String testGroup() default "";
    String testVersion() default "";
}
