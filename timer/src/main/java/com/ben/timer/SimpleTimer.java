package com.ben.timer;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * Created by wangshuhe on 2016/1/20.
 * 一个简单易用的定时器工具类
 */
public class SimpleTimer {
    private final int DEFAULT_TIME_TICK = 1000;
    private Runnable mListener = null;
    private Runnable autoTaskRunnable = null;
    private long mDelayMillis = DEFAULT_TIME_TICK;
    private int mRepeats = 0;
    private int mRunningRepeats = 0;
    private boolean executeInUiThread = true;

    /****
     * 定时器构造函数
     * @param delayMillis 定时器频率（单位ms）
     * @param repeat 定时器启动次数，如果repeat<0，则启动n次,直到显示调用stop才会停止。
     * @param listener 定时器回调
     */
    public SimpleTimer(long delayMillis, int repeat, Runnable listener){
        mDelayMillis = delayMillis;
        mListener = listener;
        mRepeats = repeat;
        if( mRepeats == 0 ){
            mRepeats = 1;
        }

        if ( mDelayMillis < 0 ){
            mDelayMillis = 0;
        }

        autoTaskRunnable = new Runnable() {
            @Override
            public void run() {
                if( null == mListener ){
                    return;
                }

                mListener.run();

                boolean continueRun = true;
                mRunningRepeats = mRunningRepeats - 1;
                if( mRunningRepeats == 0 ){
                    continueRun = false;
                }

                if ( mRunningRepeats < 0 && mRepeats > 0 ){
                    continueRun = false;
                }

                if ( mDelayMillis <= 0 ){
                    continueRun = false;
                }

                if ( continueRun ){
                    if ( executeInUiThread ){
                        HandlerHolder.autoTaskHandler.postDelayed(autoTaskRunnable, mDelayMillis);
                    }
                    else {
                        HandlerHolder.autoTaskThreadHandler.postDelayed(autoTaskRunnable, mDelayMillis);
                    }
                }
                else {
                    mRunningRepeats = 0;
                }
            }
        };
    }

    /****
     * 定时器构造函数,定时器将只热行一次
     * @param delayMillis 定时器频率（单位ms）
     * @param listener 定时器回调
     */
    public SimpleTimer(long delayMillis, Runnable listener){
        this(delayMillis, 1, listener);
    }

    /****
     * 定时器工作线程配置
     * @param inUiThread 是否在UI线程启动定时器？true, UI线程里启动，false 将定时器推送到工作者线程,
     * @return 返回当前定时器实例对象
     */
    public SimpleTimer runInUIThread( boolean inUiThread ){
        executeInUiThread = inUiThread;
        return this;
    }

    /***
     * 启动定时器
     */
    public void start( ){
        stop();
        mRunningRepeats = mRepeats;
        if ( !executeInUiThread ){
            HandlerHolder.autoTaskThreadHandler.postDelayed(autoTaskRunnable, mDelayMillis);
        }
        else {
            HandlerHolder.autoTaskHandler.postDelayed(autoTaskRunnable, mDelayMillis);
        }
    }

    /***
     * 停止定时器
     */
    public void stop(){
        mRunningRepeats = 0;
        if ( !executeInUiThread ){
            HandlerHolder.autoTaskThreadHandler.removeCallbacks(autoTaskRunnable);
        }
        else {
            HandlerHolder.autoTaskHandler.removeCallbacks(autoTaskRunnable);
        }
    }

    /**
     * 定时器是否正在运行
     * @return true 正在运行，false停止运行
     */
    public boolean running() {
        return mRunningRepeats != 0;
    }

    public long getDelay(){
        return mDelayMillis;
    }

    /**
     * 定时器工作者线程定义
     */
    private static class TimerHandleThread extends HandlerThread {
        TimerHandleThread() {
            super("bilin_thread_simple_timer");
        }
        private static class HandlerThreadHolder{
            private final static TimerHandleThread INSTANCE = new TimerHandleThread();
        }
        public static TimerHandleThread getInstance(){
            if ( !HandlerThreadHolder.INSTANCE.isAlive() ){
                synchronized (HandlerThreadHolder.INSTANCE){
                    if ( !HandlerThreadHolder.INSTANCE.isAlive() ){
                        HandlerThreadHolder.INSTANCE.start();
                    }
                }
            }
            return HandlerThreadHolder.INSTANCE;
        }
    }

    private static class HandlerHolder{
        private final static Handler autoTaskHandler= new Handler(Looper.getMainLooper());
        private final static Handler autoTaskThreadHandler = new Handler(TimerHandleThread.getInstance().getLooper());
    }
}
