package com.cdh.aop.toys.annotation;

import com.cdh.aop.toys.thread.ThreadScene;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by chidehang on 2020/6/16
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AutoThread {
    ThreadScene scene();
    boolean waitUntilDone() default true;
}
