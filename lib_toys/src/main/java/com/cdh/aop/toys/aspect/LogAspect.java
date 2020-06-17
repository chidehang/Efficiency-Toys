package com.cdh.aop.toys.aspect;

import com.cdh.aop.toys.annotation.AutoLog;
import com.cdh.aop.toys.log.LogUtils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * Created by chidehang on 2020/6/16
 */
@Aspect
public class LogAspect {

    private static final String TAG = "LogAspect";

    @Pointcut("execution (@com.cdh.aop.toys.annotation.AutoLog * *(..))")
    public void logMethodExecute() {
    }

    @Around("logMethodExecute()")
    public Object autoLog(ProceedingJoinPoint joinPoint) {
        try {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            AutoLog log = methodSignature.getMethod().getAnnotation(AutoLog.class);
            if (log != null) {
                // 日志详细信息
                StringBuilder sb = new StringBuilder();

                String methodName = methodSignature.getMethod().getName();
                sb.append(methodName);

                // 参数值
                Object[] args = joinPoint.getArgs();
                if (args != null && args.length > 0) {
                    sb.append("(");
                    for (int i=0; i<args.length; i++) {
                        sb.append(args[i]);
                        if (i != args.length-1) {
                            sb.append(",");
                        }
                    }
                    sb.append(")");
                }

                // 执行原方法
                long beginTime = System.currentTimeMillis();
                Object result = joinPoint.proceed();
                long costTime = System.currentTimeMillis() - beginTime;

                if (methodSignature.getReturnType() != void.class) {
                    sb.append(" => ").append(result);
                }

                // 耗时
                sb.append(" | ").append("cost=").append(costTime);

                // 类名和行号
                String className = methodSignature.getDeclaringType().getSimpleName();
                int srcLine = joinPoint.getSourceLocation().getLine();
                sb.append(" | [").append(className).append(":").append(srcLine).append("]");

                LogUtils.log(log.level(), log.tag(), sb.toString());

                return result;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }
}
