package com.cdh.aop.toys.aspect;

import android.util.Log;

import com.cdh.aop.toys.annotation.AutoThread;
import com.cdh.aop.toys.log.LogUtils;
import com.cdh.aop.toys.thread.ThreadScene;
import com.cdh.aop.toys.thread.ThreadUtils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * Created by chidehang on 2020/6/16
 */
@Aspect
public class ThreadAspect {

    private static final String TAG = "ThreadAspect";

    @Pointcut("execution (@com.cdh.aop.toys.annotation.AutoThread * *(..))")
    public void threadSceneTransition() {
    }

    @Around("threadSceneTransition()")
    public Object executeInThread(final ProceedingJoinPoint joinPoint) {
        final Object[] result = {null};
        try {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            AutoThread thread = methodSignature.getMethod().getAnnotation(AutoThread.class);
            if (thread != null) {
                ThreadScene threadScene = thread.scene();
                if (threadScene == ThreadScene.MAIN && !ThreadUtils.isMainThread()) {
                    // 切换到主线程执行
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                result[0] = joinPoint.proceed();
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    }, thread.waitUntilDone());
                } else if (threadScene == ThreadScene.BACKGROUND && ThreadUtils.isMainThread()) {
                    // 切换到子线程执行
                    ThreadUtils.run(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                result[0] = joinPoint.proceed();
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    }, thread.waitUntilDone());
                } else {
                    // 当前线程运行
                    result[0] = joinPoint.proceed();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result[0];
    }

    @Before("call (* java.lang.Thread.start(..))")
    public void callThreadStart(JoinPoint joinPoint) {
        try {
            Thread thread = (Thread) joinPoint.getTarget();
            if (ThreadUtils.isDefaultThreadName(thread)) {
                LogUtils.e(TAG, "发现启动线程[" + thread + "]未自定义线程名称! [" + joinPoint.getSourceLocation() + "]");
                thread.setName(thread.getName() + "-" + joinPoint.getThis());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
