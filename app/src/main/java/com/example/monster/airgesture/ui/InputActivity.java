package com.example.monster.airgesture.ui;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.example.monster.airgesture.R;
import com.example.monster.airgesture.model.db.CandidateWord;
import com.example.monster.airgesture.model.db.DictionaryDBImpl;

import java.util.List;

/**
 * Created by WelkinShadow on 2017/10/26.
 */

public class InputActivity<T extends InputContract.Presenter> extends BaseActivity<T> implements InputContract.View {

    @Override
    public T setPresenter() {
        return (T)new InputPresenterImpl(this);
    }

    @Override
    public int setLayout() {
        return R.layout.layout_main;
    }

    @Override
    public void initialize() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        getPresenter().initConfig();
        getPresenter().onAttachDB(new DictionaryDBImpl(this));
        getPresenter().findWord("123");
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
    public void setCandidateWord(List<CandidateWord> candidateWord) {

    }

    @Override
    public void clearCandidateWord() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                showMessage("拒绝访问sd卡将导致无法识别动作数据");
            }
        }
    }
}
