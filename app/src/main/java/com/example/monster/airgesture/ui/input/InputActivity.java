package com.example.monster.airgesture.ui.input;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.monster.airgesture.R;
import com.example.monster.airgesture.model.db.module.ContactedWord;
import com.example.monster.airgesture.model.db.module.Word;
import com.example.monster.airgesture.ui.test.MainActivity;
import com.example.monster.airgesture.ui.base.BaseActivity;
import com.example.monster.airgesture.utils.CapLockUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 负责展示数据的View层，处理数据的展示{@link InputContract.View}
 * 主要交互逻辑见onClick方法{@link InputActivity#onClick(View)}
 * 手势识别和数据库的交互见对应的presenter实现类{@link InputPresenterImpl}
 * Created by WelkinShadow on 2017/10/26.
 */

public class InputActivity<T extends InputContract.Presenter> extends BaseActivity<T> implements InputContract.View, View.OnClickListener, Thread.UncaughtExceptionHandler {
    private static final String TAG = "InputActivity";

    //一段时间后自动填词
    private class AutoSetWordTask implements Runnable {
        @Override
        public void run() {
            do {
                end = System.currentTimeMillis();
                Thread.yield();
            } while ((end - start) < 1500);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (adapter != null
                            && adapter.getFirst() != null
                            && !(adapter.getFirst() instanceof ContactedWord)
                            && !isTouchRecycler) {
                        setWord(adapter.getFirst().getWord());
                    }
                    isTiming = false;
                }
            });
        }
    }

    private EditText inputtedArea;
    private TextView inputStrokes;
    private RecyclerView candidateWordArea;
    private int[] buttons = {R.id.bt_on, R.id.bt_off, R.id.bt_del, R.id.bt_clear,
            R.id.bt_space, R.id.bt_num, R.id.bt_comma, R.id.bt_period};
    private Button capLocks;

    private boolean isOn = false;
    private boolean isNumKeyboard = false;

    private ExecutorService pool;
    private long start;
    private long end;
    private boolean isTouchRecycler = false;
    private boolean isTiming = false;//正在计时

    private int capStatus = 102;
    private final int FIRST_CAP = 100;
    private final int ALL_CAP = 101;
    private final int NO_CAP = 102;

    private WordAdapter<Word> adapter = null;
    //WordAdapter的回调接口，实现item的点击事件
    private WordAdapter.AdapterListener listener;

    @Override
    @SuppressWarnings("unchecked")
    public T setPresenter() {
        return (T) new InputPresenterImpl(this);
    }

    @Override
    public int setLayout() {
        return R.layout.layout_main;
    }

    @Override
    @SuppressWarnings("all")
    public void initialize() {
        pool = Executors.newSingleThreadExecutor();

        //申请权限拷贝模板数据
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        getPresenter().initConfig();

        //回调接口设置
        listener = new WordAdapter.AdapterListener() {
            @Override
            public void onClickItem(Word word) {
                setWord(word.getWord());
            }

            @Override
            public void onLongClickItem(Word word) {
            }
        };

        //在此调用下面方法，才能捕获到线程中的异常
        Thread.setDefaultUncaughtExceptionHandler(this);

        inputStrokes = (TextView) findViewById(R.id.input_strokes);
        inputtedArea = (EditText) findViewById(R.id.inputted_area);
        inputtedArea.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        candidateWordArea = (RecyclerView) findViewById(R.id.candidate_word);
        candidateWordArea.setLayoutManager(layoutManager);

        Button bt;
        for (int id : buttons) {
            bt = (Button) findViewById(id);
            bt.setOnClickListener(this);
        }

        bt = (Button) findViewById(R.id.bt_off);
        bt.setTextColor(ContextCompat.getColor(this, R.color.indigo));

        capLocks = (Button) findViewById(R.id.bt_caplock);
        capLocks.setText(getString(R.string.no_cap));
        capLocks.setOnClickListener(this);

        candidateWordArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        if (!isTouchRecycler) {
                            isTouchRecycler = true;
                            Log.i(TAG, "TOUCH : true");
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        isTouchRecycler = false;
                        if (adapter != null) {
                            submitAutoSetWord();
                        }
                        Log.i(TAG, "TOUCH : false");
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 删除单词
     */
    public void delWord() {
        Log.i(TAG, "delete word");
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

    /**
     * 设置单词
     */
    @Override
    public void setWord(String word) {
        Log.i(TAG, "set word");
        resetCapLock();
        String text = inputtedArea.getText().toString();
        String appendText;
        appendText = text.length() > 0 && !isNumKeyboard ? " " + word : word;
        inputtedArea.append(appendText);
        //数字键盘不需要清空和查找关联词
        if (!isNumKeyboard) {
            clearStroke();
            clearCandidateWord();
            Log.i(TAG, "find contacted");
            getPresenter().findContacted(word);
        }
    }

    /**
     * 清空输入区
     */
    public void clearInput() {
        Log.i(TAG, "clear input");
        inputtedArea.setText("");
    }

    /**
     * 删除笔画
     */
    @Override
    public void setStroke(int type) {
        String stroke = null;
        switch (type) {
            case 1:
                stroke = "一";
                break;
            case 2:
                stroke = " |";
                break;
            case 3:
                stroke = " /";
                break;
            case 4:
                stroke = " \\";
                break;
            case 5:
                stroke = "⊂";
                break;
            case 6:
                stroke = "⊃";
                break;
        }
        inputStrokes.append(stroke);
    }

    /**
     * 删除笔画
     */
    public void delStroke() {
        Log.i(TAG, "delete stroke");
        String strokes = inputStrokes.getText().toString();
        if (strokes.length() > 0) {
            inputStrokes.setText(strokes.subSequence(0, strokes.length() - 1));
            getPresenter().delStoker();
            if (strokes.length() == 1) {
                clearCandidateWord();
            }
        }
//        showMessage("删除笔画");
    }

    /**
     * 清空笔画区
     */
    public void clearStroke() {
        Log.i(TAG, "clear stroke");
        inputStrokes.setText("");
        getPresenter().clearStoker();
//        showMessage("清空笔画");
    }

    /**
     * 设置候选词
     */
    @Override
    @SuppressWarnings("unchecked")
    public void setCandidateWord(List<Word> words) {
        Log.i(TAG, "set candidate word");
        if (adapter == null) {
            adapter = new WordAdapter(words, listener);
            candidateWordArea.setAdapter(adapter);
        } else {
            adapter.notifyDiff(words);
        }
        //submitAutoSetWord();
    }

    /**
     * 清空候选词区
     */
    @Override
    public void clearCandidateWord() {
        Log.i(TAG, "clear candidateWords");
        if (adapter != null) {
            adapter.notifyDiff(new ArrayList<Word>());
        }
    }

    /**
     * 大小写转换
     */
    private void transformCaplock() {
        Log.i(TAG, "transform caplocks");
        String text = inputtedArea.getText().toString();
        int lastWordIndex = text.lastIndexOf(" ") == -1 ? 0 : text.lastIndexOf(" ") + 1;
        String lastWord = text.substring(lastWordIndex, text.length());
        String afterTransform = null;
        capStatus = capStatus == NO_CAP ? FIRST_CAP : capStatus + 1;
        switch (capStatus) {
            case NO_CAP:
                afterTransform = CapLockUtil.transformNoCapsAll(lastWord);
                capLocks.setTextColor(ContextCompat.getColor(this, R.color.black));
                capLocks.setText(getString(R.string.no_cap));
                break;
            case FIRST_CAP:
                afterTransform = CapLockUtil.transformCapsFirst(lastWord);
                capLocks.setTextColor(ContextCompat.getColor(this, R.color.indigo));
                capLocks.setText(getString(R.string.first_cap));
                break;
            case ALL_CAP:
                afterTransform = CapLockUtil.transformCapsAll(lastWord);
                capLocks.setTextColor(ContextCompat.getColor(this, R.color.indigo));
                capLocks.setText(getString(R.string.all_cap));
                break;
        }
        String afterText = text.substring(0, lastWordIndex) + afterTransform;
        inputtedArea.setText(afterText);
        inputtedArea.setSelection(afterText.length());
    }

    /**
     * 置位大小写锁
     */
    private void resetCapLock() {
        capStatus = NO_CAP;
        capLocks.setTextColor(ContextCompat.getColor(this, R.color.black));
        capLocks.setText(getString(R.string.no_cap));
    }

    @Override
    public void onClick(View view) {
        String strokeText = inputStrokes.getText().toString();
        String inputText = inputtedArea.getText().toString();
        Button bt;
        switch (view.getId()) {
            case R.id.bt_on:
                if (!isOn) {
                    bt = (Button) findViewById(R.id.bt_on);
                    bt.setTextColor(ContextCompat.getColor(this, R.color.indigo));
                    bt = (Button) findViewById(R.id.bt_off);
                    bt.setTextColor(ContextCompat.getColor(this, R.color.black));
                    getPresenter().startRecording();
                    showMessage("开始读取手势");
                    isOn = true;
                } else {
                    showMessage("已经开启手势读取");
                }
                break;

            case R.id.bt_off:
                if (isOn) {
                    bt = (Button) findViewById(R.id.bt_on);
                    bt.setTextColor(ContextCompat.getColor(this, R.color.black));
                    bt = (Button) findViewById(R.id.bt_off);
                    bt.setTextColor(ContextCompat.getColor(this, R.color.indigo));
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
                if (strokeText.length() > 0) {
                    clearStroke();
                } else if (inputText.length() > 0) {
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
                if (strokeText.length() > 0) {
                    delStroke();
                } else if (inputText.length() > 0) {
                    delWord();
                } else {
                    showMessage("已清空");
                }
                break;

            case R.id.bt_num:
                bt = (Button) findViewById(R.id.bt_num);
                if (isNumKeyboard) {
                    bt.setTextColor(ContextCompat.getColor(this, R.color.black));
                } else {
                    bt.setTextColor(ContextCompat.getColor(this, R.color.indigo));
                }
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

    private void submitAutoSetWord() {
        Log.i(TAG, "submit auto set word");
        start = System.currentTimeMillis();
        if (!isTiming) {
            Log.i(TAG, "submit task");
            isTiming = true;
            pool.submit(new AutoSetWordTask());
        }
    }

    @Override
    protected void onDestroy() {
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
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.e(TAG, "Exception : " + e);
    }
}
