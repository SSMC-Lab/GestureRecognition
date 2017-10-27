package com.example.monster.airgesture.ui;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.monster.airgesture.Conditions;
import com.example.monster.airgesture.GlobalConfig;
import com.example.monster.airgesture.model.db.CandidateDB;
import com.example.monster.airgesture.model.db.CandidateWord;

import java.io.File;
import java.util.List;

/**
 * Created by WelkinShadow on 2017/10/26.
 */

public class InputPresenterImpl<V extends InputContract.View> extends BasePresenterImpl<V> implements InputContract.Presenter<V> {

    //存放编码
    private static StringBuilder coding = new StringBuilder();

    private static final String TAG = "InputPresenterImpl";

    private CandidateDB db;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Conditions.MESSAGE_PHASE_MODEL:
                    int type = (int) msg.getData().getFloat(Conditions.TYPE);
                    coding.append(type);
                    findWord(coding.toString());
                    break;
            }
        }
    };

    @Override
    public void findWord(String coding) {
        if (coding != null) {
            Log.i(TAG, "Coding : " + coding);
            if (coding.length() == 1) {
                List<CandidateWord> letter = db.getLetter(coding);
                getView().setCandidateWord(letter);
            } else if (coding.length() > 1) {
                List<CandidateWord> words = db.getWordList(coding, 0);
                getView().setCandidateWord(words);
            }
        } else {
            Log.i(TAG, "Coding is null");
        }
    }

    @Override
    public void onAttachDB(CandidateDB db) {
        this.db = db;
    }

    @Override
    public void onDetachDB() {
        db = null;
    }

    @Override
    public void initConfig() {
        GlobalConfig.fAbsolutepath.mkdirs();//创建文件夹
        GlobalConfig.fTemplatePath.mkdirs();//创建文件夹
        GlobalConfig.fResultPath.mkdirs();//创建文件夹

        if (GlobalConfig.bPlayThreadFlag) {
            GlobalConfig.stWavePlayer.play();
        } else {
            GlobalConfig.isRecording = true;
        }

        GlobalConfig.stPhaseProxy.init();
        GlobalConfig.stPhaseProxy.sendHandler(handler);
    }

    @Override
    public void startRecording() {
        GlobalConfig.stWavRecorder.start();
        GlobalConfig.fPcmRecordFile2 = new File(GlobalConfig.getRecordedFileName(GlobalConfig.sRecordPcmFileName2));
    }

    @Override
    public void stopRecording() {
        GlobalConfig.isRecording = false;
        //GlobalConfig.stPhaseAudioRecord.stopRecording();
        GlobalConfig.stPhaseProxy.destroy();
        GlobalConfig.stWaveFileUtil.destroy();
        GlobalConfig.stWavRecorder.stop();
    }
}
