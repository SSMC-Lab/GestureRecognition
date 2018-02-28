package com.example.monster.airgesture.ui.input;

import android.os.Handler;
import android.os.Message;

import com.example.monster.airgesture.Conditions;
import com.example.monster.airgesture.GlobalConfig;
import com.example.monster.airgesture.data.DataProvider;
import com.example.monster.airgesture.data.IDataSource;
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
    private IDataSource dataRepository;
    private StringBuilder coding;
    private RecognitionSwitch recognitionSwitch;
    private Handler mHandler; //handler会回传phase模块解析出的手势，并递交给presenter内部处理

    public InputPresenter() {
        dataRepository = DataProvider.provideDataRepository();
        resetCurrentUser();
        recognitionSwitch = RecognitionSwitch.getInstance();
        mHandler = new HandlerUtils.HandlerHolder(this);
        coding = new StringBuilder();
        copyTemplate("heng2.txt");
        copyTemplate("shu2.txt");
        copyTemplate("youhu2.txt");
        copyTemplate("youxie2.txt");
        copyTemplate("zuohu2.txt");
        copyTemplate("zuoxie2.txt");
    }

    private void findWord(String coding) {
        if (!StringUtils.isEmpty(coding)) {
            List<Word> words = dataRepository.findWords(coding);
            getView().showWordInWordArea(words);
        } else {
            LogUtils.e("Coding is null");
        }
    }

    @Override
    public void findContactedWord(String word) {
        if (!StringUtils.isEmpty(word)) {
            List<Word> words = dataRepository.findContactedWord(word);
            getView().showWordInWordArea(words);
            LogUtils.d("set contacted word，size = " + words.size());
        }
    }

    private void receiveWord(int type) {
        if (!isNumKeyboard) {
            getView().enterStroke(type);
            coding.append(type);
            findWord(coding.toString());
            LogUtils.d("receive gesture : " + type);
        }
    }

    @Override
    public void changeNumKeyboard() {
        List<Word> words = isNumKeyboard ? new ArrayList<Word>() : dataRepository.getNum();
        getView().showWordInWordArea(words);
        isNumKeyboard = !isNumKeyboard;
    }

    @Override
    public void resetCurrentUser() {
        dataRepository.resetCurrentUser();
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
        getView().showWordInWordArea(new ArrayList<Word>());
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
