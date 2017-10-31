package com.example.monster.airgesture.ui;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.monster.airgesture.R;
import com.example.monster.airgesture.model.db.CandidateWord;
import com.example.monster.airgesture.model.db.DictionaryDBImpl;

import java.util.List;

/**
 * Created by WelkinShadow on 2017/10/26.
 */

public class InputActivity<T extends InputContract.Presenter> extends BaseActivity<T> implements InputContract.View ,View.OnClickListener {
    private TextView inputted_area;
    private TextView input_strokes;
    private Button bt_on;
    private Button bt_del;
    private Button bt_caolock;
    private Button bt_num;
    private Button bt_off;
    private Button bt_clear;
    private Button bt_space;
    private Button bt_comma;
    private Button bt_period;


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

        input_strokes = (TextView)findViewById(R.id.input_strokes);
        inputted_area = (TextView)findViewById(R.id.inputted_area);
        bt_caolock = (Button)findViewById(R.id.bt_caolock);
        bt_clear = (Button)findViewById(R.id.bt_clear);
        bt_comma = (Button)findViewById(R.id.bt_comma);
        bt_del = (Button)findViewById(R.id.bt_del);
        bt_num = (Button)findViewById(R.id.bt_num);
        bt_on = (Button)findViewById(R.id.bt_on);
        bt_off = (Button)findViewById(R.id.bt_off);
        bt_period = (Button)findViewById(R.id.bt_period);
        bt_space = (Button)findViewById(R.id.bt_space);
    }

    @Override
    public void delWord() {
        CharSequence text = inputted_area.getText();
        int flag = 0;
        for (int i = text.length()-2; ; i--)
        {
            if (i == 0)         //只有一个单词的情况
            {
                inputted_area.clearComposingText();
                break;
            }
            if (text.charAt(i) == ' ')
            {
                text = text.subSequence(0,i);       //截取最后一个单词之前的字符串;
                inputted_area.setText(text);
                break;
            }
        }
    }

    @Override
    public void setWord(String word) {
        inputted_area.append(word + " ");
    }

    @Override
    public void clearInput() {
        inputted_area.clearComposingText();
    }

    @Override
    public void setStroke(int type) {
        String stroke = null;
        switch (type)
        {
            case 1:
                stroke = "一";   break;
            case 2:
                stroke = "丨";   break;
            case 3:
                stroke = "丿";   break;
            case 4:
                stroke = "㇏";   break;
            case 5:
                stroke = "(";   break;
            case 6:
                stroke = ")";    break;
        }
        input_strokes.append(stroke);
    }

    @Override
    public void delStroke() {
        input_strokes.setText(input_strokes.getText().subSequence(0,input_strokes.getText().length()-1));
    }

    @Override
    public void clearStroke() {
        input_strokes.clearComposingText();
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

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.bt_on:
                break;
            case R.id.bt_caolock:
                break;
            case R.id.bt_clear:
                if (input_strokes.getText() == null && input_strokes.getText() == "")
                    clearInput();
                else clearStroke();
                break;
            case R.id.bt_comma:
                setWord(", ");
                break;
            case R.id.bt_del:
                if (input_strokes.getText() == null && input_strokes.getText() == "")
                    delWord();
                else delStroke();
                break;
            case R.id.bt_num:
                break;
            case R.id.bt_space:
                setWord(" ");
                break;
            case R.id.bt_period:
                setWord(". ");
                break;
            case R.id.bt_off:
                break;



        }
    }
}
