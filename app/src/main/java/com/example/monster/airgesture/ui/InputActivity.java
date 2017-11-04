package com.example.monster.airgesture.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.monster.airgesture.R;
import com.example.monster.airgesture.model.db.CandidateWord;
import com.example.monster.airgesture.model.db.DictionaryDBImpl;
import com.example.monster.airgesture.utils.CapLockUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by WelkinShadow on 2017/10/26.
 */

public class InputActivity<T extends InputContract.Presenter> extends BaseActivity<T> implements InputContract.View, View.OnClickListener, Thread.UncaughtExceptionHandler {
    private static final String TAG = "InputActivity";

    private EditText inputtedArea;
    private TextView inputStrokes;
    private RecyclerView candidateWordArea;
    private @IdRes int[] buttons = {R.id.bt_on, R.id.bt_off, R.id.bt_del, R.id.bt_clear,
            R.id.bt_space, R.id.bt_num, R.id.bt_comma, R.id.bt_period};
    private Button capLocks;

    private boolean isOn = false;
    private boolean isNumKeyboard = false;
    private boolean isTiming = false;

    private int capStatus = 102;
    private final int FIRST_CAP = 100;
    private final int ALL_CAP = 101;
    private final int NO_CAP = 102;

    private ExecutorService pool;
    private long start = 0;
    private long end = 0;

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


    private class TimeTask implements Runnable {

        @Override
        public void run() {
            long time;
            do {
                end = System.currentTimeMillis();
                time = end - start;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            } while (time < 2000);
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

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.e(TAG, "Exception : " + e);
    }

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
        pool = Executors.newSingleThreadExecutor();
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
        inputtedArea.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        candidateWordArea = (RecyclerView) findViewById(R.id.candidate_word);
        candidateWordArea.setLayoutManager(layoutManager);

        for (int id : buttons) {
            Button bt = (Button) findViewById(id);
            bt.setOnClickListener(this);
        }

        capLocks = (Button) findViewById(R.id.bt_caplock);
        capLocks.setText("caplocks");
        capLocks.setOnClickListener(this);
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
        resetCapLock();
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
            pool.submit(new TimeTask());
        }
    }

    @Override
    public void clearCandidateWord() {
        if (adapter != null) {
            adapter.notifyDiff(new ArrayList<CandidateWord>());
        }
    }


    private void transformCaplock() {
        String text = inputtedArea.getText().toString();
        int lastWordIndex = text.lastIndexOf(" ") == -1 ? 0 : text.lastIndexOf(" ") + 1;
        String lastWord = text.substring(lastWordIndex, text.length());
        String afterTransform = null;
        capStatus = capStatus == NO_CAP ? FIRST_CAP : capStatus + 1;
        if (capStatus == NO_CAP) {
            afterTransform = CapLockUtil.transformNoCapsAll(lastWord);
            capLocks.setTextColor(ContextCompat.getColor(this, R.color.black));
            capLocks.setText("caplocks");
        } else if (capStatus == FIRST_CAP) {
            afterTransform = CapLockUtil.transformCapsFirst(lastWord);
            capLocks.setTextColor(ContextCompat.getColor(this, R.color.indigo));
            capLocks.setText("Caplocks");
        } else if (capStatus == ALL_CAP) {
            afterTransform = CapLockUtil.transformCapsAll(lastWord);
            capLocks.setTextColor(ContextCompat.getColor(this, R.color.indigo));
            capLocks.setText("CAPLOCKS");
        }
        String afterText = text.substring(0, lastWordIndex) + afterTransform;
        inputtedArea.setText(afterText);
        inputtedArea.setSelection(afterText.length());
    }

    private void resetCapLock(){
        capStatus = NO_CAP;
        capLocks.setTextColor(ContextCompat.getColor(this, R.color.black));
        capLocks.setText("caplocks");
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
                transformCaplock();
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
                setWord(",");
                break;

            case R.id.bt_period:
                setWord(".");
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

            case R.id.inputted_area:
                resetCapLock();
                break;

        }
    }

    @Override
    protected void onDestroy() {
        getPresenter().onDetachDB();
        pool.shutdown();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_test) {
            //跳转到InputActivity
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
