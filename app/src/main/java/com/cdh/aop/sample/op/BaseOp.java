package com.cdh.aop.sample.op;

/**
 * Created by chidehang on 2020/6/16
 * 操作基类
 */
public abstract class BaseOp {

    public static final String TAG = "BaseOp";

    private BaseOp next;

    public BaseOp(BaseOp next) {
        this.next = next;
    }

    public int operate(int value) {
        int result = onOperate(value);
        if (next != null) {
            result = next.operate(result);
        }
        return result;
    }

    protected abstract int onOperate(int value);
}
