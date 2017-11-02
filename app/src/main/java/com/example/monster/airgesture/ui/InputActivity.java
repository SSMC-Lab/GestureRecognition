package com.example.monster.airgesture.ui;

import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.monster.airgesture.R;
import com.example.monster.airgesture.model.db.CandidateWord;
import com.example.monster.airgesture.model.db.DictionaryDBImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by WelkinShadow on 2017/10/26.
 */

public class InputActivity<T extends InputContract.Presenter> extends BaseActivity<T> implements InputContract.View, View.OnClickListener, Thread.UncaughtExceptionHandler {
    private EditText inputtedArea;
    private TextView inputStrokes;
    private RecyclerView candidateWordArea;
    private
    @IdRes
    int[] buttons = {R.id.bt_on, R.id.bt_off, R.id.bt_del, R.id.bt_clear,
            R.id.bt_caplock, R.id.bt_space, R.id.bt_num, R.id.bt_comma, R.id.bt_period};

    private boolean isOn = false;
    private boolean isNumKeyboard = false;
    private boolean isTiming = false;

    private static final String TAG = "InputActivity";


 /*   private Button bt_on;
    private Button bt_del;
    private Button bt_caolock;
    private Button bt_num;
    private Button bt_off;
    private Button bt_clear;
    private Button bt_space;
    private Button bt_comma;
    private Button bt_period;*/

    private WordAdapter<CandidateWord> adapter = null;
    private WordAdapter.AdapterListener listener = new WordAdapter.AdapterListener() {
        @Override
        public void onClickItem(CandidateWord word) {
            setWord(word.getWord());
            if (!isNumKeyboard) {
                clearStroke();
                clearCandidateWord();
            }
        }

        @Override
        public void onLongClickItem(CandidateWord word) {

        }
    };

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.e(TAG, "Exception : " + e);
    }


    private class TimeTask implements Runnable {

        @Override
        public void run() {
            long time;
            do {
                end = System.currentTimeMillis();
                time = end - start;
            } while (time < 1500);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (adapter.getFirst() != null) {
                        setWord(adapter.getFirst().getWord());
                        clearStroke();
                        clearCandidateWord();
                    }
                    isTiming = false;
                }
            });
        }

    }

    private ExecutorService pool = Executors.newSingleThreadExecutor();
    private long start = 0;
    private long end = 0;

    @Override
    public T setPresenter() {
        return (T) new InputPresenterImpl(this);
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

        //在此调用下面方法，才能捕获到线程中的异常
        Thread.setDefaultUncaughtExceptionHandler(this);

        inputStrokes = (TextView) findViewById(R.id.input_strokes);
        inputtedArea = (EditText) findViewById(R.id.inputted_area);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        candidateWordArea = (RecyclerView) findViewById(R.id.candidate_word);
        candidateWordArea.setLayoutManager(layoutManager);

        for (int id : buttons) {
            Button bt = (Button) findViewById(id);
            bt.setOnClickListener(this);
        }

        /*
        bt_caolock = (Button) findViewById(R.id.bt_caplock);
        bt_clear = (Button) findViewById(R.id.bt_clear);
        bt_comma = (Button) findViewById(R.id.bt_comma);
        bt_del = (Button) findViewById(R.id.bt_del);
        bt_num = (Button) findViewById(R.id.bt_num);
        bt_on = (Button) findViewById(R.id.bt_on);
        bt_off = (Button) findViewById(R.id.bt_off);
        bt_period = (Button) findViewById(R.id.bt_period);
        bt_space = (Button) findViewById(R.id.bt_space);*/
    }

    @Override
    public void delWord() {
        String text = inputtedArea.getText().toString();

        //删除最后一个单词
        int lastIndex = text.lastIndexOf(" ");
        if (lastIndex == -1) {
            clearInput();
        } else {
            text = text.substring(0, text.lastIndexOf(" "));
            inputtedArea.setText(text);
            inputtedArea.setSelection(text.length());
        }

        //删除最后一个字符
        /*text = text.substring(0,text.length()-1);*/

    }

    @Override
    public void setWord(String word) {
        String text = inputtedArea.getText().toString();
        if (text != null && text.length() > 0) {
            inputtedArea.append(" " + word);
        } else {
            inputtedArea.append(word);
        }

    }

    @Override
    public void clearInput() {
        inputtedArea.setText("");
    }

    @Override
    public void setStroke(int type) {
        String stroke = null;
        switch (type) {
            case 1:
                stroke = "一";
                break;
            case 2:
                stroke = "丨";
                break;
            case 3:
                stroke = "丿";
                break;
            case 4:
                stroke = "㇏";
                break;
            case 5:
                stroke = "(";
                break;
            case 6:
                stroke = ")";
                break;
        }
        inputStrokes.append(stroke);
    }

    @Override
    public void delStroke() {
        String strokes = inputStrokes.getText().toString();
        if (strokes != null && strokes.length() > 0) {
            inputStrokes.setText(strokes.subSequence(0, strokes.length() - 1));
            getPresenter().delStoker();
            if (strokes.length() == 1) {
                clearCandidateWord();
            }
            Log.d(TAG, inputStrokes.getText() + "");
        }
//        showMessage("删除笔画");
    }

    @Override
    public void clearStroke() {
        inputStrokes.setText("");
        getPresenter().clearStoker();
//        showMessage("清空笔画");
    }

    @Override
    public void setCandidateWord(List<CandidateWord> candidateWords) {
        if (adapter == null) {
            adapter = new WordAdapter<>(candidateWords, listener);
            candidateWordArea.setAdapter(adapter);
        } else {
            adapter.notifyDiff(candidateWords);
        }
        start = System.currentTimeMillis();
        if (!isTiming) {
            isTiming = true;
            pool.execute(new TimeTask());
        }
    }

    @Override
    public void clearCandidateWord() {
        if (adapter != null) {
            adapter.notifyDiff(new ArrayList<CandidateWord>());
        }
    }


    @Override
    public boolean isNumKeyboard() {
        return isNumKeyboard;
    }

    @Override
    public void onClick(View view) {
        String strokeText = inputStrokes.getText().toString();
        String inputText = inputtedArea.getText().toString();
        switch (view.getId()) {
            case R.id.bt_on:
                if (!isOn) {
                    getPresenter().startRecording();
                    showMessage("开始读取手势");
                    isOn = true;
                } else {
                    showMessage("已经开启手势读取");
                }
                break;

            case R.id.bt_off:
                if (isOn) {
                    getPresenter().stopRecording();
                    showMessage("关闭识别功能");
                    isOn = false;
                } else {
                    showMessage("已经关闭");
                }
                break;

            case R.id.bt_caplock:
                showMessage("未实现");
                break;

            case R.id.bt_clear:
                if (strokeText != null && strokeText.length() > 0) {
                    clearStroke();
                } else if (inputText != null && inputText.length() > 0) {
                    clearInput();
                }
                showMessage("已清空");
                break;

            case R.id.bt_comma:
                setWord(", ");
                break;

            case R.id.bt_period:
                setWord(". ");
                break;

            case R.id.bt_del:
                if (strokeText != null && strokeText.length() > 0) {
                    delStroke();
                } else if (inputText != null && inputText.length() > 0) {
                    delWord();
                } else {
                    showMessage("已清空");
                }
                break;

            case R.id.bt_num:
                clearStroke();
                getPresenter().changeNumKeyboard();
                isNumKeyboard = !isNumKeyboard;
                break;

            case R.id.bt_space:
                setWord(" ");
                break;

        }
    }

    @Override
    protected void onDestroy() {
        getPresenter().onDetachDB();
        super.onDestroy();
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
