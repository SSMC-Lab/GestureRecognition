package com.example.monster.airgesture.ui.input;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.monster.airgesture.Conditions;
import com.example.monster.airgesture.R;
import com.example.monster.airgesture.data.bean.CandidateWord;
import com.example.monster.airgesture.data.bean.Word;
import com.example.monster.airgesture.timer.TimerHelper;
import com.example.monster.airgesture.timer.TimerProcessor;
import com.example.monster.airgesture.ui.PresenterFactory;
import com.example.monster.airgesture.ui.test.MainActivity;
import com.example.monster.airgesture.ui.base.BaseActivity;
import com.example.monster.airgesture.ui.user.UserActivity;
import com.example.monster.airgesture.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 负责展示数据的View层，处理数据的展示{@link IInputContract.View}
 * Created by WelkinShadow on 2017/10/26.
 */

public class InputActivity extends BaseActivity<IInputContract.Presenter> implements
        IInputContract.View, View.OnClickListener, View.OnTouchListener {

    private static final String TAG = "InputActivity";

    private EditText inputtedArea;
    private TextView inputStrokes;
    private RecyclerView candidateWordArea;
    private int[] buttons = {R.id.bt_on, R.id.bt_off, R.id.bt_del, R.id.bt_clear,
            R.id.bt_space, R.id.bt_num, R.id.bt_comma, R.id.bt_period};
    private Button capLocks;

    //定时器管理
    private TimerHelper timerHelper;

    private boolean isAutoSetWord = true;//是否开启自动上墙功能
    //自动输入功能的定时时间
    private final int AUTO_INPUT_MILLI = 2000;

    private boolean isOn = false;//识别开关是否打开
    private boolean isNumKeyboard = false;//数字键盘是否在显示
    private boolean isTouchRecycler = false;//候选词区域是否正被触摸
    private boolean isTiming = false;//计时器是否正在工作

    //大小写状态位
    private final int FIRST_CAP = 0x100;
    private final int ALL_CAP = 0x101;
    private final int NO_CAP = 0x102;
    private int capStatus = NO_CAP;

    private WordAdapter<Word> candidateWordAdapter;

    @Override
    public IInputContract.Presenter setPresenter() {
        return PresenterFactory.getInputPresenter();
    }

    @Override
    public int setLayout() {
        return R.layout.layout_main;
    }

    @Override
    protected int getMenuId() {
        return R.menu.menu_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //申请权限拷贝模板数据
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        timerHelper = new TimerHelper(AUTO_INPUT_MILLI, new TimerProcessor() {
            @Override
            public void process() {
                setWordByAuto();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().refreshCurrentUser();
    }

    @Override
    public void initViews() {
        inputStrokes = findView(R.id.input_strokes);
        inputtedArea = findView(R.id.inputted_area);
        inputtedArea.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        candidateWordArea = findView(R.id.candidate_word);
        candidateWordArea.setLayoutManager(layoutManager);
        candidateWordAdapter = new WordAdapter<>(new ArrayList<Word>(), new WordAdapter.OnItemClickListener() {
            @Override
            public void onClickItem(Word word) {
                enterWord(word.getWord());
            }

            @Override
            public void onLongClickItem(Word word) {
            }
        });
        candidateWordArea.setAdapter(candidateWordAdapter);
        candidateWordArea.setOnTouchListener(this);

        Button bt;
        for (int id : buttons) {
            bt = findView(id);
            bt.setOnClickListener(this);
        }
        bt = findView(R.id.bt_off);
        bt.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));

        capLocks = findView(R.id.bt_caplock);
        capLocks.setText(getString(R.string.no_cap));
        capLocks.setOnClickListener(this);
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
     * 单词输入
     */
    public void enterWord(String word) {
        Log.i(TAG, "input word :" + word);
        resetCapLock();
        String text = inputtedArea.getText().toString();
        String appendText = text.length() > 0 && !isNumKeyboard ? " " + word : word;
        inputtedArea.append(appendText);
        //数字键盘不需要清空和查找关联词
        if (!isNumKeyboard) {
            clearStroke();
            clearCandidateWord();
            getPresenter().findContacted(word);
            Log.i(TAG, "find contacted");
        }
    }

    /**
     * 清空输入区
     */
    private void clearInput() {
        Log.i(TAG, "clear input");
        inputtedArea.setText("");
    }

    /**
     * 删除笔画
     */
    @Override
    public void setStroke(int stokes) {
        Log.i(TAG, "set stroke");
        //返回对应type的Stokes字符
        inputStrokes.append(Conditions.STOKES[stokes - 1]);
    }

    /**
     * 删除笔画
     */
    private void deleteStroke() {
        Log.i(TAG, "delete stroke");
        String strokes = inputStrokes.getText().toString();
        if (strokes.length() > 0) {
            inputStrokes.setText(strokes.subSequence(0, strokes.length() - 1));
            getPresenter().delStoker();
            if (strokes.length() == 1) {
                clearCandidateWord();
            }
        }
    }

    /**
     * 清空笔画区
     */
    private void clearStroke() {
        Log.i(TAG, "clear stroke");
        inputStrokes.setText("");
        getPresenter().clearStoker();
    }

    /**
     * 设置候选词
     */
    @Override
    public void setCandidateWord(List<Word> words) {
        Log.i(TAG, "set candidate word");
        candidateWordAdapter.notifyDiff(words);
        startTimerTask();
    }

    /**
     * 启动定时任务
     * 下一句可以自动上墙
     * 即等待一段时间后首单词自动输入
     */
    private void startTimerTask() {
        if (isAutoSetWord) {
            cancelCurrentTimerTask();
            timerHelper.startTimer();
            isTiming = true;
        }
    }

    /**
     * 下一句自动上墙主逻辑
     */
    private void setWordByAuto() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Word word = candidateWordAdapter.getFirst();
                if (word != null &&
                        word instanceof CandidateWord &&
                        !isNumKeyboard &&
                        !isTouchRecycler) {
                    isTiming = false;
                    enterWord(candidateWordAdapter.getFirst().getWord());
                }
            }
        });
    }

    /**
     * 取消当前的计时任务
     */
    private void cancelCurrentTimerTask() {
        if (isTiming) {
            timerHelper.stopTimer();
            isTiming = false;
        }
    }

    /**
     * 清空候选词区
     */
    public void clearCandidateWord() {
        Log.i(TAG, "clear candidateWords");
        if (candidateWordAdapter != null) {
            candidateWordAdapter.notifyDiff(new ArrayList<Word>());
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
        switch (capStatus) {
            case NO_CAP:
                capStatus = FIRST_CAP;
                break;
            case FIRST_CAP:
                capStatus = ALL_CAP;
                break;
            case ALL_CAP:
                capStatus = NO_CAP;
                break;
        }
        switch (capStatus) {
            case NO_CAP:
                afterTransform = StringUtils.lowerText(lastWord);
                capLocks.setTextColor(ContextCompat.getColor(this, R.color.black));
                capLocks.setText(getString(R.string.no_cap));
                break;
            case FIRST_CAP:
                afterTransform = StringUtils.upperFirstLetter(lastWord);
                capLocks.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                capLocks.setText(getString(R.string.first_cap));
                break;
            case ALL_CAP:
                afterTransform = StringUtils.upperText(lastWord);
                capLocks.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                capLocks.setText(getString(R.string.all_cap));
                break;
        }
        afterTransform = text.substring(0, lastWordIndex) + afterTransform;
        inputtedArea.setText(afterTransform);
        inputtedArea.setSelection(afterTransform.length());
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
            //开启识别
            case R.id.bt_on:
                if (!isOn) {
                    bt = findView(R.id.bt_on);
                    bt.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                    bt = findView(R.id.bt_off);
                    bt.setTextColor(ContextCompat.getColor(this, R.color.black));
                    getPresenter().startRecording();
                    showMessage("ON");
                    isOn = true;
                } else {
                    showMessage("已经开启手势读取");
                }
                break;

            //关闭识别
            case R.id.bt_off:
                if (isOn) {
                    bt = findView(R.id.bt_on);
                    bt.setTextColor(ContextCompat.getColor(this, R.color.black));
                    bt = findView(R.id.bt_off);
                    bt.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                    getPresenter().stopRecording();
                    showMessage("OFF");
                    isOn = false;
                } else {
                    showMessage("已经关闭");
                }
                break;

            //大小写切换
            case R.id.bt_caplock:
                transformCaplock();
                break;

            //clear按钮
            case R.id.bt_clear:
                if (strokeText.length() > 0) {
                    clearStroke();
                } else if (inputText.length() > 0) {
                    clearInput();
                }
                //showMessage("Clear");
                break;

            case R.id.bt_comma:
                enterWord(",");
                break;

            case R.id.bt_period:
                enterWord(".");
                break;

            //删除按钮
            case R.id.bt_del:
                if (strokeText.length() > 0) {
                    deleteStroke();
                } else if (inputText.length() > 0) {
                    delWord();
                } else {
                    // showMessage("Delete");
                }
                break;

            //数字键盘
            case R.id.bt_num:
                bt = (Button) findViewById(R.id.bt_num);
                if (isNumKeyboard) {
                    bt.setTextColor(ContextCompat.getColor(this, R.color.black));
                } else {
                    bt.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                }
                clearStroke();
                getPresenter().changeNumKeyboard();
                isNumKeyboard = !isNumKeyboard;
                break;

            case R.id.bt_space:
                enterWord(" ");
                break;

            case R.id.inputted_area:
                resetCapLock();
                break;

        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_test) {
            //跳转到InputActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_user) {
            Intent intent = new Intent(this, UserActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.candidate_word) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    isTouchRecycler = true;
                    break;
                case MotionEvent.ACTION_UP:
                    isTouchRecycler = false;
                    startTimerTask();
                    break;
            }
        }
        v.performClick();
        return false;
    }
}