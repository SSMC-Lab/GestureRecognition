package com.example.monster.airgesture.ui.input;

import com.example.monster.airgesture.Conditions;
import com.example.monster.airgesture.data.DataProvider;
import com.example.monster.airgesture.data.IDataSource;
import com.example.monster.airgesture.data.bean.Word;
import com.example.monster.airgesture.phase.IPhaseBiz;
import com.example.monster.airgesture.phase.PhaseBizProvider;
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
    private StringBuilder coding;
    private IPhaseBiz phaseBiz;

    public InputPresenter() {
        dataRepository = DataProvider.provideDataRepository();
        phaseBiz = PhaseBizProvider.providePhaseBiz();
        resetCurrentUser();
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
        phaseBiz.startRecognition(new IPhaseBiz.PhaseListener() {
            @Override
            public void receiveActionType(float type) {
                receiveWord((int) type);
            }
        });
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
            LogUtils.d("delete stoker,now coding = \"" + coding + "\"");
        }
    }

    /**
     * 拷贝模板数据
     */
    private void copyTemplate(String templateName) {
        FileCopyUtils.copyInAssets(templateName, Conditions.sFileTemplatePath + templateName);
    }
}
