package com.cdh.aop.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.cdh.aop.sample.op.BaseOp;
import com.cdh.aop.sample.op.log.AddOpWithLog;
import com.cdh.aop.sample.op.log.DivOpWithLog;
import com.cdh.aop.sample.op.log.MulOpWithLog;
import com.cdh.aop.sample.op.log.SubOpWithLog;
import com.cdh.aop.sample.op.normal.AddOp;
import com.cdh.aop.sample.op.normal.DivOp;
import com.cdh.aop.sample.op.normal.MulOp;
import com.cdh.aop.sample.op.normal.SubOp;
import com.cdh.aop.sample.op.thread.AddOpInThread;
import com.cdh.aop.sample.op.thread.DivOpInThread;
import com.cdh.aop.sample.op.thread.MulOpInThread;
import com.cdh.aop.sample.op.thread.SubOpInThread;
import com.cdh.aop.toys.annotation.AutoLog;
import com.cdh.aop.toys.log.LogLevel;
import com.cdh.aop.toys.privacy.PrivacyController;
import com.cdh.aop.toys.thread.ThreadUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "toys";

    private CheckBox cbUserAllow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cbUserAllow = findViewById(R.id.checkbox);
        PrivacyController.setUserAllow(cbUserAllow.isChecked());
        cbUserAllow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrivacyController.setUserAllow(isChecked);
            }
        });
    }

    public void doNormal(View view) {
        BaseOp div = new DivOp(null);
        BaseOp mul = new MulOp(div);
        BaseOp sub = new SubOp(mul);
        BaseOp add = new AddOp(sub);
        int result = add.operate(100);
        Toast.makeText(this, result+"", Toast.LENGTH_SHORT).show();
    }

    @AutoLog(tag = BaseOp.TAG, level = LogLevel.DEBUG)
    public void doWithLog(View view) {
        BaseOp div = new DivOpWithLog(null);
        BaseOp mul = new MulOpWithLog(div);
        BaseOp sub = new SubOpWithLog(mul);
        BaseOp add = new AddOpWithLog(sub);
        int result = add.operate(100);
        Toast.makeText(this, result+"", Toast.LENGTH_SHORT).show();
    }

    public void doWithThread(View view) {
        BaseOp div = new DivOpInThread(null);
        BaseOp mul = new MulOpInThread(div);
        BaseOp sub = new SubOpInThread(mul);
        BaseOp add = new AddOpInThread(sub);
        int result = add.operate(100);
        Toast.makeText(this, result+"", Toast.LENGTH_SHORT).show();
    }

    public void renameThreadName(View view) {
        new Thread(new PrintNameRunnable()).start();

        Thread t = new Thread(new PrintNameRunnable());
        t.setName("myname-thread-test");
        t.start();
    }

    public void interceptPrivacy(View view) {
        Log.d(TAG, "用户同意: " + PrivacyController.isUserAllowed());

        List<ApplicationInfo> applicationInfos = DeviceUtils.getInstalledApplications(this);
        if (applicationInfos != null && applicationInfos.size() > 5) {
            applicationInfos = applicationInfos.subList(0, 5);
        }
        Log.d(TAG, "getInstalledApplications: " + applicationInfos);

        List<PackageInfo> packageInfos = DeviceUtils.getInstalledPackages(this);
        if (packageInfos != null && packageInfos.size() > 5) {
            packageInfos = packageInfos.subList(0, 5);
        }
        Log.d(TAG, "getInstalledPackages: " + packageInfos);

        Log.d(TAG, "getImei: " + DeviceUtils.getImeiValue(this));
        Log.d(TAG, "getLine1Number: " + DeviceUtils.getLine1Number(this));
        Log.d(TAG, "getLastKnownLocation: " + DeviceUtils.getLastKnownLocation(this));

        try {
            Log.d(TAG, "loadClass: " + getClassLoader().loadClass("com.cdh.aop.sample.op.BaseOp"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            PackageManager pm = getPackageManager();
            Method method = PackageManager.class.getDeclaredMethod("getInstalledApplications", int.class);
            List<ApplicationInfo> list = (List<ApplicationInfo>) method.invoke(pm, 0);
            if (list != null && list.size() > 5) {
                list = list.subList(0, 5);
            }
            Log.d(TAG, "reflect getInstalledApplications: " + list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class PrintNameRunnable implements Runnable {
        @Override
        public void run() {
            Log.d(TAG, "thread name: " + Thread.currentThread().getName());
        }
    }
}
