package com.example.monster.airgesture.ui.input;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.monster.airgesture.Conditions;
import com.example.monster.airgesture.GlobalConfig;
import com.example.monster.airgesture.model.db.WordQuery;
import com.example.monster.airgesture.model.db.Word;
import com.example.monster.airgesture.ui.base.BasePresenterImpl;
import com.example.monster.airgesture.utils.FileCopyUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Presenter实现类
 * 处理和View层和后台的逻辑交互
 * Created by WelkinShadow on 2017/10/26.
 */

public class InputPresenterImpl<V extends InputContract.View> extends BasePresenterImpl<V> implements InputContract.Presenter<V> {

    private static final String TAG = "InputPresenterImpl";

    private boolean isNumKeyboard = false;

    private StringBuilder coding = new StringBuilder();
    private Context context;
    private WordQuery db;

    private ExecutorService pool;

    /**
     * 这个handler会回传phase模块解析出的手势，并递交给presenter内部处理
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Conditions.MESSAGE_PHASE_MODEL:
                    receiveWord((int) msg.getData().getFloat(Conditions.TYPE));
                    break;
            }
        }
    };

    public InputPresenterImpl(Context context) {
        this.context = context;
        pool = Executors.newSingleThreadExecutor();
    }

    @Override
    public void findWord(String coding) {
        Log.i(TAG, "find word");
        if (coding != null) {
            Log.i(TAG, "Coding : " + coding);
            if (coding.length() > 0) {
                List<Word> words = db.getWords(coding);
                getView().setCandidateWord(words);
            }
        } else {
            Log.i(TAG, "Coding is null");
        }
    }

    @Override
    public void findContacted(String word) {
        if (word != null) {
            List<Word> words = db.getContacted(word);
            getView().setCandidateWord(words);
            Log.i(TAG, "set contacted word，size = " + words.size());
        }
    }

    private void receiveWord(int type) {
        if (!isNumKeyboard) {
            getView().setStroke(type);
            coding.append(type);
            findWord(coding.toString());
            Log.i(TAG, "receive gesture : " + type);
        }
    }

    @Override
    public void changeNumKeyboard() {
        Log.i(TAG, "change num keyboard ");
        if (isNumKeyboard) {
            getView().setCandidateWord(new ArrayList<Word>());
        } else {
            getView().setCandidateWord(db.getNum());
        }
        isNumKeyboard = !isNumKeyboard;
    }

    @Override
    public void onAttachDB(WordQuery db) {
        this.db = db;
    }

    @Override
    public void onDetachDB() {
        db = null;
    }

    @Override
    public void initConfig() {
        if (!Conditions.configInit) {
            GlobalConfig.fAbsolutepath.mkdirs();//创建文件夹
            GlobalConfig.fTemplatePath.mkdirs();//创建文件夹
            GlobalConfig.fResultPath.mkdirs();//创建文件夹

            //initIos();
            if (GlobalConfig.bPlayThreadFlag) {
                //ThreadInstantPlay threadInstantPlay = new ThreadInstantPlay();
                //Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                //threadInstantPlay.start();
                GlobalConfig.stWavePlayer.play();
            } else {
                GlobalConfig.isRecording = true;
            }

            //startRecordAction();

            GlobalConfig.stPhaseProxy.init();

        }
        GlobalConfig.stPhaseProxy.sendHandler(handler);
        copyTemplate("heng2.txt");
        copyTemplate("shu2.txt");
        copyTemplate("youhu2.txt");
        copyTemplate("youxie2.txt");
        copyTemplate("zuohu2.txt");
        copyTemplate("zuoxie2.txt");
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

    @Override
    public void clearStoker() {
        coding.delete(0, coding.length());
        getView().setCandidateWord(new ArrayList<Word>());
        Log.d(TAG, "clear stoker,now coding = " + coding);
    }

    @Override
    public void delStoker() {
        Log.d(TAG, "coding length:" + coding.length());
        if (coding.length() > 0) {
            coding.delete(coding.length() - 1, coding.length());
            findWord(coding.toString());
            Log.d(TAG, "delete stoker,now coding = \"" + coding + "\"");
        }
    }

    //拷贝用于解析的模板数据
    private void copyTemplate(String templateName) {
        File template = new File(GlobalConfig.sFileTemplatePath + templateName);
        try {
            FileCopyUtil.copy(context.getAssets().open(templateName), new FileOutputStream(template));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
