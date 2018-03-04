package com.example.monster.airgesture.phase;

import android.media.AudioFormat;

/**
 * 依赖注入
 */

public class PhaseBizProvider {
    private static final int AUDIO_SAMPLE_RATE = 44100;//44100;//16000;//48000
    private static final int AUDIO_PLAY_FREQ = 20000;//44100;//16000;//48000

    /**
     * 提供{@link IPhaseBiz}实例
     *
     * @return {@link IPhaseBiz}的实例对象
     */
    public static IPhaseBiz providePhaseBiz() {
        return new PhaseBiz(
                new PhaseProxy(),
                new PlayerTask(AUDIO_PLAY_FREQ, AUDIO_SAMPLE_RATE),
                new RecorderTask(AudioFormat.CHANNEL_IN_MONO, AUDIO_SAMPLE_RATE, AudioFormat.ENCODING_PCM_16BIT));
    }
}
