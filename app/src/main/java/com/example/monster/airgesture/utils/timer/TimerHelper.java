package com.example.monster.airgesture.utils.timer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 定时器管理类
 * Created by WelkinShadow on 2017/11/18.
 */

public class TimerHelper {
    private TimerProcessor mProcessor;
    private int mDelayMs;
    private Timer mTimer;
    private TimerTask mTimerTask;

    /**
     * 构造函数
     *
     * @param delayMs   延时
     * @param processor 定时处理器，由调用者定制实现
     */
    public TimerHelper(int delayMs, TimerProcessor processor) {
        mProcessor = processor;
        mDelayMs = delayMs;
    }

    /**
     * 启动定时器
     */
    public void startTimer() {
        mTimer = new Timer(true);
        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                if (mProcessor != null) {
                    mProcessor.process();
                }
            }

        };

        mTimer.schedule(mTimerTask, mDelayMs);
    }

    /**
     * 停止定时器
     */
    public void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

}

