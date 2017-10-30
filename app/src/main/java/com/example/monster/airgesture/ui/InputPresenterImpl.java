package com.example.monster.airgesture.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.monster.airgesture.Conditions;
import com.example.monster.airgesture.GlobalConfig;
import com.example.monster.airgesture.model.db.CandidateDB;
import com.example.monster.airgesture.model.db.CandidateWord;
import com.example.monster.airgesture.utils.FileCopyUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by WelkinShadow on 2017/10/26.
 */

public class InputPresenterImpl<V extends InputContract.View> extends BasePresenterImpl<V> implements InputContract.Presenter<V> {

    //存放编码
    private static StringBuilder coding = new StringBuilder();
    private Context context;

    public static final String TAG = "InputPresenterImpl";

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

    public InputPresenterImpl(Context context) {
        this.context = context;
    }

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

        copyTemplete("heng2.txt");
        copyTemplete("shu2.txt");
        copyTemplete("youhu2.txt");
        copyTemplete("youxie2.txt");
        copyTemplete("zuohu2.txt");
        copyTemplete("zuoxie2.txt");
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

    private void copyTemplete(String templeteName) {
        String path = GlobalConfig.sFileTemplatePath + templeteName;
        File templete = new File(path);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        if (!templete.exists()) {
            try {
                templete.createNewFile();
                inputStream = context.getAssets().open(templeteName);
                outputStream = new FileOutputStream(templete);
                boolean result = FileCopyUtil.copy(inputStream, outputStream);
                if (!result) {
                    Log.e(TAG, "copy error : " + templeteName + " failed");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }

        }
    }
}
