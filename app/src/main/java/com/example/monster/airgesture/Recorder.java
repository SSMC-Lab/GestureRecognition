package com.example.monster.airgesture;

import android.media.AudioFormat;
import java.text.SimpleDateFormat;

/**
 * Created by FruitBasket on 2017/5/27.
 */

public abstract class Recorder {

    protected int channelIn;//用于指定音频的来源
    protected int sampleRate;//声音的采样频率
    protected int encoding;//指定每一个声音帧的位大小

    public Recorder(){
        this(
                AudioFormat.CHANNEL_IN_MONO,
                GlobalConfig.AUDIO_SAMPLE_RATE,
                AudioFormat.ENCODING_PCM_16BIT
        );
    }

    public Recorder(int channelIn, int sampleRate, int encoding){
        this.channelIn=channelIn;
        this.sampleRate=sampleRate;
        this.encoding=encoding;
    }

    /**
     * 开始录音
     * @return true 成功开始录音
     */
    public abstract void start();

    /**
     * 停止录音
     * @return true 成功停止录音
     */
    public abstract boolean stop();


}
