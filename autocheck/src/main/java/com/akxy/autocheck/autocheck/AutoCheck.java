package com.akxy.autocheck.autocheck;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author wangp
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoCheck {

    long value() default 15;

    TimeUnit timeUnit() default TimeUnit.MINUTES;
}
