package com.example.monster.airgesture.ui.input;

import com.example.monster.airgesture.GlobalConfig;
import com.example.monster.airgesture.data.DataProvider;
import com.example.monster.airgesture.data.IDataSource;
import com.example.monster.airgesture.data.bean.Word;
import com.example.monster.airgesture.phase.ActionTypeListener;
import com.example.monster.airgesture.phase.PhaseBiz;
import com.example.monster.airgesture.ui.base.BasePresenter;
import com.example.monster.airgesture.utils.FileCopyUtils;
import com.example.monster.airgesture.utils.LogUtils;
import com.example.monster.airgesture.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter实现类 {@link IInputContract.Presenter}
 * Created by WelkinShadow on 2017/10/26.
 */

public class InputPresenter<V extends IInputContract.View> extends BasePresenter<V>
        implements IInputContract.Presenter<V> {

    private boolean isNumKeyboard = false;
    private IDataSource dataRepository;
    private StringBuilder coding = new StringBuilder();
    private PhaseBiz phaseBiz;
    //    private Handler mHandler; //handler会回传phase模块解析出的手势，并递交给presenter内部处理
    private ActionTypeListener mListener = new ActionTypeListener() {//解析模块的的回调接口
        @Override
        public void receiveActionType(float type) {
            if (!isNumKeyboard) {
                getView().enterStroke((int)type);
                coding.append(type);
                findWord(coding.toString());
                LogUtils.d("receive gesture : " + type);
            }
        }
    };

    public InputPresenter() {
        dataRepository = DataProvider.provideDataRepository();
        phaseBiz = PhaseBiz.getInstance();
//        mHandler = new HandlerUtils.HandlerHolder(this);
        copyTemplate("heng2.txt");
        copyTemplate("shu2.txt");
        copyTemplate("youhu2.txt");
        copyTemplate("youxie2.txt");
        copyTemplate("zuohu2.txt");
        copyTemplate("zuoxie2.txt");
//        resetCurrentUser();
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

    @Override
    public void changeNumKeyboard() {
        isNumKeyboard = !isNumKeyboard;
        List<Word> words = isNumKeyboard ? dataRepository.getNum() : new ArrayList<Word>();
        getView().showWordInWordArea(words);
    }

    @Override
    public void resetCurrentUser() {
        dataRepository.resetCurrentUser();
    }

    @Override
    public void startRecording() {
        phaseBiz.startRecognition(mListener);
    }

    @Override
    public void stopRecording() {
        phaseBiz.stopRecognition();
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
        }
    }

    /**
     * 拷贝用于解析的模板数据
     */
    private void copyTemplate(String templateName) {
        FileCopyUtils.copyInAssets(templateName, GlobalConfig.sFileTemplatePath + templateName);
    }
}
