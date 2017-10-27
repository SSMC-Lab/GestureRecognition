package com.example.monster.airgesture.ui;

import com.example.monster.airgesture.R;

import java.util.List;

/**
 * Created by WelkinShadow on 2017/10/26.
 */

public class InputActivity<T extends InputContract.Presenter> extends BaseActivity<T> implements InputContract.View{

    @Override
    public T setPresenter() {
        return (T) new InputPresenterImpl();
    }

    @Override
    public int setLayout() {
        return R.layout.layout_main;
    }

    @Override
    public void initialize() {
        getPresenter().initConfig();
    }

    @Override
    public void delWord() {

    }

    @Override
    public void setWord(String word) {

    }

    @Override
    public void clearInput() {

    }

    @Override
    public void setStroke(int type) {

    }

    @Override
    public void delStroke() {

    }

    @Override
    public void clearStroke() {

    }

    @Override
    public void setCandidateWord(List<String> candidateWord) {

    }

    @Override
    public void clearCandidateWord() {

    }
}
