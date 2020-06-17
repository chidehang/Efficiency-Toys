package com.cdh.aop.toys.annotation;

import com.cdh.aop.toys.log.LogLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 打印日志
 *
 * Created by chidehang on 2020/6/16
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AutoLog {

    String tag();

    LogLevel level() default LogLevel.VERBOSE;
}
