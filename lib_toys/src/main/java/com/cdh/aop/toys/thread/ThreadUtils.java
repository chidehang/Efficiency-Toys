package com.cdh.aop.toys.thread;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Pair;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Created by chidehang on 2020/6/16
 */
public class ThreadUtils {

    private static final String THREAD_NAME_PREFIX = "toys-thread-";

    private static final int WHAT_RUN_ON_MAIN = 1;

    private volatile static ExecutorService sExecutorService;
    private volatile static MainHandler sMainHandler;

    public static boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    /**
     * 判断线程名称是否是默认名称
     */
    public static boolean isDefaultThreadName(Thread thread) {
        String name = thread.getName();
        String pattern = "^Thread-[1-9]\\d*$";
        return Pattern.matches(pattern, name);
    }

    /**
     * 子线程执行
     *
     * @param runnable 待执行任务
     * @param block 是否等待执行完成
     */
    public static void run(final Runnable runnable, final boolean block) {
        Future future = getExecutorService().submit(new Runnable() {
            @Override
            public void run() {
                runnable.run();
            }
        });

        if (block) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 主线程执行
     *
     * @param runnable 待执行任务
     * @param block 是否等待执行完成
     */
    public static void runOnMainThread(Runnable runnable, boolean block) {
        if (isMainThread()) {
            runnable.run();
            return;
        }

        CountDownLatch latch = null;
        if (block) {
            latch = new CountDownLatch(1);
        }
        Pair<Runnable, CountDownLatch> pair = new Pair<>(runnable, latch);
        getMainHandler().obtainMessage(WHAT_RUN_ON_MAIN, pair).sendToTarget();

        if (block) {
            try {
                latch.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static ExecutorService getExecutorService() {
        if (sExecutorService == null) {
            synchronized (ThreadUtils.class) {
                if (sExecutorService == null) {
                    sExecutorService = Executors.unconfigurableExecutorService(
                            new ThreadPoolExecutor(1, 20,
                                    3, TimeUnit.SECONDS,
                                    new SynchronousQueue<Runnable>(),
                                    new DefaultThreadFactory())
                    );
                }
            }
        }
        return sExecutorService;
    }

    public static MainHandler getMainHandler() {
        if (sMainHandler == null) {
            synchronized (ThreadUtils.class) {
                if (sMainHandler == null) {
                    sMainHandler = new MainHandler();
                }
            }
        }
        return sMainHandler;
    }

    private static class DefaultThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName(THREAD_NAME_PREFIX + t.getId());
            return t;
        }
    }

    private static class MainHandler extends Handler {

        MainHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_RUN_ON_MAIN) {
                Pair<Runnable, CountDownLatch> pair = (Pair<Runnable, CountDownLatch>) msg.obj;
                try {
                    pair.first.run();
                } finally {
                    if (pair.second != null) {
                        pair.second.countDown();
                    }
                }
            }
        }
    }
}
