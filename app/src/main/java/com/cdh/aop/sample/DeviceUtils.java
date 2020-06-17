package com.cdh.aop.sample;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import java.util.List;

/**
 * Created by chidehang on 2020/6/17
 */
public class DeviceUtils {

    public static List<ApplicationInfo> getInstalledApplications(Context context) {
        return context.getPackageManager().getInstalledApplications(0);
    }

    public static List<PackageInfo> getInstalledPackages(Context context) {
        return context.getPackageManager().getInstalledPackages(0);
    }

    public static String getImeiValue(Context context) {
        String deviceId = "";
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        deviceId = telephonyManager.getImei();
                    } else {
                        deviceId = telephonyManager.getDeviceId();
                    }
                }
            }
            return deviceId;
        } catch (Exception exception) {
            exception.printStackTrace();
            return deviceId;
        }
    }

    public static String getLine1Number(Context context) {
        String deviceId = "";
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager != null) {
                    deviceId = telephonyManager.getLine1Number();
                }
            }
            return deviceId;
        } catch (Exception exception) {
            exception.printStackTrace();
            return deviceId;
        }
    }

    public static Location getLastKnownLocation(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }
}
