package com.example.monster.airgesture.phase;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * 使用播放器发出指定频率{@link RecorderTask#sampleRate}的正弦波
 */

public class PlayerTask {
    private int waveRate;
    private int sampleRate;

    private AudioTrack audioTrack;

    PlayerTask(int waveRate, int sampleRate) {
        this.waveRate = waveRate;
        this.sampleRate = sampleRate;
    }

    public void start() {
        final int bufferSize = 3 * AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM);

        audioTrack.play();

        new Thread(new Runnable() {
            @Override
            public void run() {
                double sampleCountInWave = sampleRate / (double) waveRate;//每一个波中，包含的样本点数量
                short[] wave = new short[bufferSize];
                int index = 0;
                while (audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                    for (int i = 0; i < wave.length; ++i, ++index) {
                        wave[i] = (short) (Short.MAX_VALUE / 2 *
                                Math.sin(2.0 * Math.PI * index / sampleCountInWave)
                        );
                    }
                    audioTrack.write(wave, 0, wave.length);
                }
            }
        }).start();

    }

    public void stop() {
        if (audioTrack != null) {
            audioTrack.stop();
            audioTrack.release();
        }
    }
}
