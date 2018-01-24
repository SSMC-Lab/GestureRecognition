package com.example.monster.airgesture.ui.input;

import android.os.Handler;
import android.os.Message;

import com.example.monster.airgesture.Conditions;
import com.example.monster.airgesture.GlobalConfig;
import com.example.monster.airgesture.data.DataFactory;
import com.example.monster.airgesture.data.IUserDAO;
import com.example.monster.airgesture.data.IWordDAO;
import com.example.monster.airgesture.data.bean.Word;
import com.example.monster.airgesture.phase.RecognitionSwitch;
import com.example.monster.airgesture.ui.base.BasePresenter;
import com.example.monster.airgesture.utils.FileCopyUtils;
import com.example.monster.airgesture.utils.HandlerUtils;
import com.example.monster.airgesture.utils.LogUtils;
import com.example.monster.airgesture.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter实现类 {@link IInputContract.Presenter}
 * Created by WelkinShadow on 2017/10/26.
 */

public class InputPresenter<V extends IInputContract.View> extends BasePresenter<V>
        implements IInputContract.Presenter<V>, HandlerUtils.OnReceiveMessageListener {

    private boolean isNumKeyboard = false;
    private StringBuilder coding;
    private IWordDAO mWordDAO;
    private IUserDAO mUserDAO;
    private RecognitionSwitch recognitionSwitch;
    private Handler mHandler; //handler会回传phase模块解析出的手势，并递交给presenter内部处理

    public InputPresenter() {
        mWordDAO = DataFactory.getWordDAO();
        mUserDAO = DataFactory.getUserDAO();
        resetCurrentUser();
        recognitionSwitch = RecognitionSwitch.getInstance();
        mHandler = new HandlerUtils.HandlerHolder(this);
        coding = new StringBuilder();
        initConfig();
    }

    private void findWord(String coding) {
        LogUtils.i("find word");
        if (!StringUtils.isEmpty(coding)) {
            List<Word> words = mWordDAO.getWords(coding);
            getView().setWordInView(words);
        } else {
            LogUtils.e("Coding is null");
        }
    }

    @Override
    public void findContactedWord(String word) {
        if (!StringUtils.isEmpty(word)) {
            List<Word> words = mWordDAO.getContacted(word);
            getView().setWordInView(words);
            LogUtils.i("set contacted word，size = " + words.size());
        }
    }

    private void receiveWord(int type) {
        if (!isNumKeyboard) {
            getView().setStroke(type);
            coding.append(type);
            findWord(coding.toString());
            LogUtils.i("receive gesture : " + type);
        }
    }

    @Override
    public void changeNumKeyboard() {
        LogUtils.i("change num keyboard ");
        List<Word> words = isNumKeyboard ? new ArrayList<Word>() : mWordDAO.getNum();
        getView().setWordInView(words);
        isNumKeyboard = !isNumKeyboard;
    }

    @Override
    public void resetCurrentUser() {
        mWordDAO.attachUser(mUserDAO.getCurrentUser());
    }

    private void initConfig() {
        copyTemplate("heng2.txt");
        copyTemplate("shu2.txt");
        copyTemplate("youhu2.txt");
        copyTemplate("youxie2.txt");
        copyTemplate("zuohu2.txt");
        copyTemplate("zuoxie2.txt");
    }

    @Override
    public void startRecording() {
        recognitionSwitch.startRecognition(mHandler);
    }

    @Override
    public void stopRecording() {
        recognitionSwitch.stopRecognition();
    }

    @Override
    public void clearStoker() {
        coding.delete(0, coding.length());
        getView().setWordInView(new ArrayList<Word>());
        LogUtils.d("clear stoker");
    }

    @Override
    public void delStoker() {
        if (coding.length() > 0) {
            coding.delete(coding.length() - 1, coding.length());
            findWord(coding.toString());
            LogUtils.d("delete stoker,now coding = \"" + coding + "\"");
        }
    }

    /**
     * 拷贝用于解析的模板数据
     */
    private void copyTemplate(String templateName) {
        FileCopyUtils.copyInAssets(templateName, GlobalConfig.sFileTemplatePath + templateName);
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
