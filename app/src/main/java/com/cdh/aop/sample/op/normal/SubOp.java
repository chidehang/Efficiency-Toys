package com.cdh.aop.sample.op.normal;

import com.cdh.aop.sample.op.BaseOp;

import java.util.Random;

/**
 * Created by chidehang on 2020/6/16
 */
public class SubOp extends BaseOp {

    public SubOp(BaseOp next) {
        super(next);
    }

    @Override
    protected int onOperate(int value) {
        return value - new Random().nextInt(10);
    }
}
