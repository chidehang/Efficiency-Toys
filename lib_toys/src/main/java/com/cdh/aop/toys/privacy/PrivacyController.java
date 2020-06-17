package com.cdh.aop.toys.privacy;

/**
 * Created by chidehang on 2020/6/17
 */
public class PrivacyController {

    private volatile static boolean sUserAllowed;

    public static void setUserAllow(boolean allow) {
        sUserAllowed = allow;
    }

    public static boolean isUserAllowed() {
        return sUserAllowed;
    }
}
