package com.zcx.arouter_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 跳转界面时的参数传递注解
 * 此注解用于成员变量且必须时public 访问权限
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface Paramenter {

      String fiendName ()default "";
}
