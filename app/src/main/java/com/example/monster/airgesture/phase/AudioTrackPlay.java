package com.example.monster.airgesture.phase;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.example.monster.airgesture.GlobalConfig;

import static java.lang.Math.PI;

/**
 * Created by Administrator on 2017/6/21.
 */

public class AudioTrackPlay {
    private AudioTrack audioTrack;
    private byte generatedSound[] ;
    public int iDataLen = 8192;

    public AudioTrackPlay() {
        init();
    }
    public void init(){
        int bufferSize = AudioTrack.getMinBufferSize(GlobalConfig.AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        iDataLen = bufferSize *3;
        iDataLen = 96000;
        generatedSound = new byte[2 * iDataLen];
        genTone();
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, GlobalConfig.AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSound.length, AudioTrack.MODE_STATIC);
        setFrequency();
    }

    //sets to new frequency and continues playing
    public void changeFrequency() {
        setFrequency();
        play();
    }

    //sets frequency and stops sound
    public void setFrequency() {

        audioTrack.write(generatedSound, 0, generatedSound.length);
        audioTrack.setLoopPoints(0, generatedSound.length/2, -1);
        //audioTrack.setLoopPoints(0, 512, -1);
    }

    public void play() {
        //16 bit because it's supported by all phones
        audioTrack.play();
    }
    public void pause() {
        audioTrack.pause();
    }

    public void stop() {
        audioTrack.stop();
        audioTrack.release();
    }

    void genTone(){
        // fill out the array

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.

        int idx = 0;
        int iIndex = 0;
        while (iIndex< iDataLen) {
            // in 16 bit wav PCM, first byte is the low order byte
            short val = (short)(32767/2*(Math.sin(2*PI*iIndex/GlobalConfig.AUDIO_SAMPLE_RATE*GlobalConfig.AUDIO_PLAY_FREQ)));
            iIndex++;
            generatedSound[idx++] = (byte) (val & 0x00ff);
            //Log.i("playdata","["+idx+"],"+generatedSound[idx-1] );
            generatedSound[idx++] = (byte) ((val & 0xff00) >>> 8);
            //Log.i("playdata","["+idx+"],"+generatedSound[idx-1] );
        }
        //GlobalConfig.stWaveFileUtil.saveDataToWav(generatedSound,GlobalConfig.stWaveFileUtil.getAndroidPlayFileName(),(long)(GlobalConfig.AUDIO_SAMPLE_RATE),1,generatedSound.length,(short)16);
    }
}
