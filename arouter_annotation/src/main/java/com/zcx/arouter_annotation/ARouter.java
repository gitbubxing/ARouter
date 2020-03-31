package com.zcx.arouter_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 路由注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ARouter {

    String Path();

    String Group() default "";
}
