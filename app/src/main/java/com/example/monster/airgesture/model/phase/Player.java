package com.example.monster.airgesture.model.phase;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by FruitBasket on 2017/5/26.
 */

public abstract class Player {
    private final static String TAG=".play.Player";

    public static final int CHANNEL_OUT_LEFT=0x1;//只输出到耳机的左边
    public static final int CHANNEL_OUT_RIGHT=0x2;//只输出到耳机的右边
    public static final int CHANNEL_OUT_BOTH=0x3;//输出到耳机的两边

    @IntDef({
            CHANNEL_OUT_LEFT,
            CHANNEL_OUT_RIGHT,
            CHANNEL_OUT_BOTH
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChannelOut{}

    @ChannelOut protected int channelOut;//控制声音输出的目标位置

    public Player(){
        this(CHANNEL_OUT_BOTH);
    }

    /**
     * @param channelOut
    */
    public Player(@ChannelOut int channelOut){
        this.channelOut=channelOut;
    }

    /**
     * 播放
     */
    public abstract void play();

    /**
     * 停止
     */
    public abstract void stop();

    /**
     * 释放这个播放器
     */
    public abstract void release();
}
