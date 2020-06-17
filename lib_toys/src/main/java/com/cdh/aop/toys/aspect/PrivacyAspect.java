package com.cdh.aop.toys.aspect;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.util.Log;

import com.cdh.aop.toys.log.LogUtils;
import com.cdh.aop.toys.privacy.PrivacyController;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by chidehang on 2020/6/17
 */
@Aspect
public class PrivacyAspect {

    private static final String TAG = "PrivacyAspect";

    private static final String POINT_CUT_GET_INSTALLED_APPLICATION = "call (* android.content.pm.PackageManager.getInstalledApplications(..))";
    private static final String POINT_CUT_GET_INSTALLED_PACKAGES = "call (* android.content.pm.PackageManager.getInstalledPackages(..))";

    private static final String POINT_CUT_GET_IMEI = "call (* android.telephony.TelephonyManager.getImei(..))";
    private static final String POINT_CUT_GET_DEVICE_ID = "call(* android.telephony.TelephonyManager.getDeviceId(..))";

    private static final String POINT_CUT_GET_LINE_NUMBER = "call (* android.telephony.TelephonyManager.getLine1Number(..))";

    private static final String POINT_CUT_GET_LAST_KNOWN_LOCATION = "call (* android.location.LocationManager.getLastKnownLocation(..))";
    private static final String POINT_CUT_REQUEST_LOCATION_UPDATES = "call (* android.location.LocationManager.requestLocationUpdates(..))";
    private static final String POINT_CUT_REQUEST_LOCATION_SINGLE = "call (* android.location.LocationManager.requestSingleUpdate(..))";

    private static final String POINT_CUT_DEX_FIND_CLASS = "call (* java.lang.ClassLoader.loadClass(..))";

    private static final String POINT_CUT_METHOD_INVOKE = "call (* java.lang.reflect.Method.invoke(..))";
    private static final List<String> REFLECT_METHOD_BLACKLIST = Arrays.asList(
            "getInstalledApplications",
            "getInstalledPackages",
            "getImei",
            "getDeviceId",
            "getLine1Number",
            "getLastKnownLocation",
            "loadClass"
    );

    @Around(POINT_CUT_GET_INSTALLED_APPLICATION)
    public Object callGetInstalledApplications(ProceedingJoinPoint joinPoint) {
        return handleProceedingJoinPoint(joinPoint, new ArrayList<ApplicationInfo>());
    }

    @Around(POINT_CUT_GET_INSTALLED_PACKAGES)
    public Object callGetInstalledPackages(ProceedingJoinPoint joinPoint) {
        return handleProceedingJoinPoint(joinPoint, new ArrayList<PackageInfo>());
    }

    @Around(POINT_CUT_GET_IMEI)
    public Object callGetImei(ProceedingJoinPoint joinPoint) {
        return handleProceedingJoinPoint(joinPoint, "");
    }

    @Around(POINT_CUT_GET_DEVICE_ID)
    public Object callGetDeviceId(ProceedingJoinPoint joinPoint) {
        return handleProceedingJoinPoint(joinPoint, "");
    }

    @Around(POINT_CUT_GET_LINE_NUMBER)
    public Object callGetLine1Number(ProceedingJoinPoint joinPoint) {
        return handleProceedingJoinPoint(joinPoint, "");
    }

    @Around(POINT_CUT_GET_LAST_KNOWN_LOCATION)
    public Object callGetLastKnownLocation(ProceedingJoinPoint joinPoint) {
        return handleProceedingJoinPoint(joinPoint, null);
    }

    @Around(POINT_CUT_REQUEST_LOCATION_UPDATES)
    public void callRequestLocationUpdates(ProceedingJoinPoint joinPoint) {
        handleProceedingJoinPoint(joinPoint, null);
    }

    @Around(POINT_CUT_REQUEST_LOCATION_SINGLE)
    public void callRequestSingleUpdate(ProceedingJoinPoint joinPoint) {
        handleProceedingJoinPoint(joinPoint, null);
    }

    @Around(POINT_CUT_DEX_FIND_CLASS)
    public Object callLoadClass(ProceedingJoinPoint joinPoint) {
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(joinPoint.getThis()).append("中动态加载");
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            sb.append("\"").append(args[0]).append("\"");
        }
        sb.append("得到").append(result);
        sb.append(" ").append(joinPoint.getSourceLocation());
        LogUtils.w(TAG, sb.toString());

        return result;
    }

    @Around(POINT_CUT_METHOD_INVOKE)
    public Object callReflectInvoke(ProceedingJoinPoint joinPoint) {
        String methodName = ((Method) joinPoint.getTarget()).getName();
        if (REFLECT_METHOD_BLACKLIST.contains(methodName)) {
            return handleProceedingJoinPoint(joinPoint, null);
        }

        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    private Object handleProceedingJoinPoint(ProceedingJoinPoint joinPoint, Object fakeResult) {
        if (!PrivacyController.isUserAllowed()) {
            StringBuilder sb = new StringBuilder();
            sb.append("用户未同意时执行了").append(joinPoint.getSignature().toShortString())
                    .append(" [").append(joinPoint.getSourceLocation()).append("]");
            LogUtils.e(TAG,  sb.toString());
            return fakeResult;
        }

        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return fakeResult;
    }
}
