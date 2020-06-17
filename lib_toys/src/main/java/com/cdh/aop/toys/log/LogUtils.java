package com.cdh.aop.toys.log;

import android.util.Log;

/**
 * Created by chidehang on 2020/6/16
 */
public class LogUtils {

    public static void log(LogLevel level, String tag, String msg) {
        switch (level) {
            case DEBUG:
                LogUtils.d(tag, msg);
                break;

            case INFO:
                LogUtils.i(tag, msg);
                break;

            case WARN:
                LogUtils.w(tag, msg);
                break;

            case ERROR:
                LogUtils.e(tag, msg);
                break;

            case VERBOSE:
            default:
                LogUtils.v(tag, msg);
                break;
        }
    }

    public static void v(String tag, String msg) {
        Log.v(tag, msg);
    }

    public static void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }
}
