package com.cdh.aop.sample.op.thread;

import android.util.Log;

import com.cdh.aop.sample.op.BaseOp;
import com.cdh.aop.toys.annotation.AutoThread;
import com.cdh.aop.toys.thread.ThreadScene;

import java.util.Random;

/**
 * Created by chidehang on 2020/6/16
 */
public class MulOpInThread extends BaseOp {

    public MulOpInThread(BaseOp next) {
        super(next);
    }

    @Override
    @AutoThread(scene = ThreadScene.MAIN)
    protected int onOperate(int value) {
        Log.w(BaseOp.TAG, "MulOpInThread onOperate: " + java.lang.Thread.currentThread());
        return value * new Random().nextInt(10);
    }
}
