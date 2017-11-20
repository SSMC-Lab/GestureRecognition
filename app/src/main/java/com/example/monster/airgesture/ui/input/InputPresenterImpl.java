package com.example.monster.airgesture.ui.input;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.monster.airgesture.Conditions;
import com.example.monster.airgesture.GlobalConfig;
import com.example.monster.airgesture.data.WordQuery;
import com.example.monster.airgesture.data.bean.Word;
import com.example.monster.airgesture.ui.base.BasePresenterImpl;
import com.example.monster.airgesture.utils.FileCopyUtil;
import com.example.monster.airgesture.utils.HandlerUtil;
import com.example.monster.airgesture.utils.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Presenter实现类
 * 处理和View层和后台的逻辑交互
 * Created by WelkinShadow on 2017/10/26.
 */

public class InputPresenterImpl<V extends InputContract.View> extends BasePresenterImpl<V>
        implements InputContract.Presenter<V>, HandlerUtil.OnReceiveMessageListener {

    private static final String TAG = "InputPresenterImpl";
    private boolean isNumKeyboard = false;
    private StringBuilder coding = new StringBuilder();
    private WordQuery db;

    //这个handler会回传phase模块解析出的手势，并递交给presenter内部处理
    private Handler handler = new HandlerUtil.HandlerHolder(this);

    /**
     * 访问数据模块查找序列对应的单词
     *
     * @param coding 待查找序列
     */
    @Override
    public void findWord(String coding) {
        Log.i(TAG, "find word");
        if (!StringUtil.isEmpty(coding)) {
            List<Word> words = db.getWords(coding);
            getView().setCandidateWord(words);
        } else {
            Log.i(TAG, "Coding is null");
        }
    }

    /**
     * 查找单词对应的关联词
     *
     * @param word 待查找关联词的原单词
     */
    @Override
    public void findContacted(String word) {
        if (!StringUtil.isEmpty(word)) {
            List<Word> words = db.getContacted(word);
            getView().setCandidateWord(words);
            Log.i(TAG, "set contacted word，size = " + words.size());
        }
    }

    /**
     * 接收到手势信息，递交处理
     *
     * @param type 手势类型
     */
    private void receiveWord(int type) {
        if (!isNumKeyboard) {
            getView().setStroke(type);
            coding.append(type);
            findWord(coding.toString());
            Log.i(TAG, "receive gesture : " + type);
        }
    }

    /**
     * 数字键盘转换处理
     */
    @Override
    public void changeNumKeyboard() {
        Log.i(TAG, "change num keyboard ");
        List<Word> words = isNumKeyboard ? new ArrayList<Word>() : db.getNum();
        getView().setCandidateWord(words);
        isNumKeyboard = !isNumKeyboard;
    }

    @Override
    public void attachQueryModel(WordQuery wordQuery) {
        db = wordQuery;
    }

    @Override
    public void detachQueryModel() {
        db = null;
    }

    /**
     * 初始化解析模块
     */
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

    /**
     * 清空序列
     */
    @Override
    public void clearStoker() {
        coding.delete(0, coding.length());
        getView().setCandidateWord(new ArrayList<Word>());
        Log.d(TAG, "clear stoker");
    }

    /**
     * 删除序列最后一个字符
     */
    @Override
    public void delStoker() {
        if (coding.length() > 0) {
            coding.delete(coding.length() - 1, coding.length());
            findWord(coding.toString());
            Log.d(TAG, "delete stoker,now coding = \"" + coding + "\"");
        }
    }

    /**
     * 拷贝用于解析的模板数据
     */
    private void copyTemplate(String templateName) {
        FileCopyUtil.copyInAssets(templateName, GlobalConfig.sFileTemplatePath + templateName);
    }

    @Override
    public void handlerMessage(Message msg) {
        switch (msg.what) {
            case Conditions.MESSAGE_PHASE_MODEL:
                receiveWord((int) msg.getData().getFloat(Conditions.TYPE));
                break;
        }
    }
}
