package com.cdh.aop.sample.op.log;

import com.cdh.aop.sample.op.BaseOp;
import com.cdh.aop.toys.log.LogLevel;
import com.cdh.aop.toys.annotation.AutoLog;

import java.util.Random;

/**
 * Created by chidehang on 2020/6/16
 */
public class DivOpWithLog extends BaseOp {

    public DivOpWithLog(BaseOp next) {
        super(next);
    }

    @Override
    @AutoLog(tag=TAG, level=LogLevel.DEBUG)
    protected int onOperate(int value) {
        return value / (new Random().nextInt(10)+1);
    }
}
