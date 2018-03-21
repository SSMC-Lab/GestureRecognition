package com.example.monster.airgesture.input;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.monster.airgesture.GlobalConfig;
import com.example.monster.airgesture.R;
import com.example.monster.airgesture.data.bean.CandidateWord;
import com.example.monster.airgesture.data.bean.Word;
import com.example.monster.airgesture.utils.timer.TimerHelper;
import com.example.monster.airgesture.utils.timer.TimerProcessor;
import com.example.monster.airgesture.base.BaseActivity;
import com.example.monster.airgesture.setting.SettingActivity;
import com.example.monster.airgesture.user.UserActivity;
import com.example.monster.airgesture.utils.SPUtils;
import com.example.monster.airgesture.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 展示数据的View层，处理数据的展示{@link IInputContract.View}
 * Created by WelkinShadow on 2017/10/26.
 */

public class InputActivity extends BaseActivity<IInputContract.Presenter> implements IInputContract.View {

    //控件

    @BindView(R.id.inputted_area)
    EditText inputtedArea;
    @BindView(R.id.input_strokes)
    TextView inputStrokes;
    @BindView(R.id.candidate_word)
    RecyclerView candidateWordArea;
    @BindView(R.id.bt_caplock)
    Button capLocks;
    @BindView(R.id.bt_off)
    Button off;
    @BindView(R.id.bt_on)
    Button on;
    @BindView(R.id.bt_num)
    Button num;

    //定时器，处理自动输入首单词的任务
    private TimerHelper mTimerHelper;

    //大小写状态位描述
    private static final int FIRST_CAP = 0x100;
    private static final int ALL_CAP = 0x101;
    private static final int NO_CAP = 0x102;
    private int capStatus = NO_CAP;
    //控制位描述
    private boolean isOn = false;//识别开关是否打开
    private boolean isNumKeyboard = false;//数字键盘是否在显示
    private boolean isTouchRecycler = false;//候选词区域是否正被触摸
    private boolean isTiming = false;//计时器是否正在工作
    //适配器
    private WordAdapter<Word> candidateWordAdapter;

    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_input;
    }

    @Override
    public IInputContract.Presenter setPresenter() {
        return new InputPresenter();
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
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //设置自动输入首单词的等待时间
        int autoInputWait = (int) SPUtils.get(getString(R.string.key_auto_enter_wait), 2);
        autoInputWait *= 1000;//转化为毫秒
        getPresenter().resetCurrentUser();
        mTimerHelper = new TimerHelper(autoInputWait, new TimerProcessor() {
            @Override
            public void process() {
                enterFirstWordAuto();
            }
        });
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void initViews() {
        //设置候选词区域
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        candidateWordArea.setLayoutManager(layoutManager);
        candidateWordAdapter = new WordAdapter<>(new ArrayList<Word>(),
                new WordAdapter.OnItemClickListener() {
                    @Override
                    public void onClickItem(Word word) {
                        enterWord(word.getWord());
                    }

                    @Override
                    public void onLongClickItem(Word word) {
                    }
                });
        candidateWordArea.setAdapter(candidateWordAdapter);
        candidateWordArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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
                v.performClick();
                return false;
            }
        });

        off.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        capLocks.setText(getString(R.string.no_cap));
    }

    /**
     * 展示候选词
     */
    @Override
    public void showWordInWordArea(List<Word> words) {
        candidateWordAdapter.notifyDiff(words);
        startTimerTask();
    }

    /**
     * 单词输入
     */
    public void enterWord(String word) {
        resetCapLock();
        //如果输入的单词不在开头且不是数字，要加一个空格来分割单词
        String text = !StringUtils.isEmpty(inputtedArea.getText().toString())
                && !isNumKeyboard ? " " + word : word;
        inputtedArea.append(text);

        //非数字键盘需要清空候选词区和查找关联词
        if (!isNumKeyboard) {
            clearStrokeArea();
            clearCandidateWord();
            getPresenter().findContactedWord(word);
        }
    }

    /**
     * 删除单词
     */
    public void delWord() {
        String text = inputtedArea.getText().toString();
        //删除最后一个单词
        int lastIndex = text.lastIndexOf(" ");
        if (lastIndex == -1) {
            clearInput();
        } else {
            text = text.substring(0, lastIndex);
            inputtedArea.setText(text);
            inputtedArea.setSelection(text.length());
        }

        //删除最后一个字符
        /*text = text.substring(0,text.length()-1);*/

    }

    /**
     * 清空输入区
     */
    private void clearInput() {
        inputtedArea.setText("");
    }

    /**
     * 输入笔画
     */
    @Override
    public void enterStroke(int stokes) {
        //返回对应type的Stokes字符
        if (inputStrokes.getVisibility() == View.GONE) {
            inputStrokes.setVisibility(View.VISIBLE);
        }
        inputStrokes.append(GlobalConfig.STOKES[stokes - 1]);
    }

    /**
     * 删除笔画
     */
    private void deleteLastStroke() {
        String strokes = inputStrokes.getText().toString();
        if (!StringUtils.isEmpty(strokes)) {
            if (strokes.length() == 1) {
                clearCandidateWord();
                clearStrokeArea();
            }
            inputStrokes.setText(strokes.subSequence(0, strokes.length() - 1));
            getPresenter().delStoker();
        }
    }

    /**
     * 清空笔画区
     */
    private void clearStrokeArea() {
        inputStrokes.setVisibility(View.GONE);
        inputStrokes.setText("");
        getPresenter().clearStoker();
    }

    /**
     * 清空候选词区
     */
    public void clearCandidateWord() {
        if (candidateWordAdapter != null) {
            candidateWordAdapter.notifyDiff(new ArrayList<Word>());
        }
    }

    /**
     * 启动定时任务
     * 下一次可以自动输入候选词区首单词
     * 即等待一段时间后首单词自动输入
     */
    private void startTimerTask() {
        boolean isAutoInput = (boolean) SPUtils.get(getString(R.string.key_auto_enter), false);
        if (isAutoInput) {
            cancelCurrentTimerTask();
            mTimerHelper.startTimer();
            isTiming = true;
        }
    }

    /**
     * 取消当前的计时任务
     */
    private void cancelCurrentTimerTask() {
        if (isTiming) {
            mTimerHelper.stopTimer();
            isTiming = false;
        }
    }

    /**
     * 下一次可以自动输入候选词区首单词
     */
    private void enterFirstWordAuto() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Word word = candidateWordAdapter.getFirst();
                if (word != null && word instanceof CandidateWord && !isNumKeyboard && !isTouchRecycler) {
                    isTiming = false;
                    enterWord(candidateWordAdapter.getFirst().getWord());
                }
            }
        });
    }

    /**
     * 最后一位字母大小写转换
     */
    @OnClick(R.id.bt_caplock)
    void transformLastWordCap() {
        String text = inputtedArea.getText().toString();
        int color;
        int lastWordFirst = text.lastIndexOf(" ") + 1;
        String lastWord = text.substring(lastWordFirst, text.length());
        switch (capStatus) {
            case NO_CAP:
                capStatus = FIRST_CAP;
                lastWord = StringUtils.upperFirstLetter(lastWord);
                color = R.color.colorAccent;
                capLocks.setText(getString(R.string.first_cap));
                break;
            case FIRST_CAP:
                capStatus = ALL_CAP;
                lastWord = StringUtils.upperText(lastWord);
                color = R.color.colorAccent;
                capLocks.setText(getString(R.string.all_cap));
                break;
            case ALL_CAP:
                capStatus = NO_CAP;
                lastWord = StringUtils.lowerText(lastWord);

                color = R.color.black;
                capLocks.setText(getString(R.string.no_cap));
                break;
            default:
                color = R.color.black;
                break;
        }
        capLocks.setTextColor(ContextCompat.getColor(this, color));
        lastWord = text.substring(0, lastWordFirst) + lastWord;
        inputtedArea.setText(lastWord);
        inputtedArea.setSelection(lastWord.length());
    }

    /**
     * 置位大小写锁
     */
    @OnClick(R.id.inputted_area)
    void resetCapLock() {
        capStatus = NO_CAP;
        capLocks.setTextColor(ContextCompat.getColor(this, R.color.black));
        capLocks.setText(getString(R.string.no_cap));
    }

    @OnClick({R.id.bt_on, R.id.bt_off, R.id.bt_del, R.id.bt_clear, R.id.bt_space, R.id.bt_num,
            R.id.bt_comma, R.id.bt_period})
    void onClick(View view) {
        String strokeText = inputStrokes.getText().toString();
        String inputText = inputtedArea.getText().toString();
        switch (view.getId()) {
            case R.id.bt_on:
                if (!isOn) {
                    on.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                    off.setTextColor(ContextCompat.getColor(this, R.color.black));
                    getPresenter().startRecording();
                    showMessage("ON");
                    isOn = true;
                } else
                    showMessage("已经开启手势读取");
                break;
            case R.id.bt_off:
                if (isOn) {
                    on.setTextColor(ContextCompat.getColor(this, R.color.black));
                    off.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                    getPresenter().stopRecording();
                    showMessage("OFF");
                    isOn = false;
                } else
                    showMessage("已经关闭");
                break;
            case R.id.bt_clear:
                if (!StringUtils.isEmpty(strokeText))
                    clearStrokeArea();
                else if (!StringUtils.isEmpty(inputText))
                    clearInput();
                break;
            case R.id.bt_del:
                if (!StringUtils.isEmpty(strokeText))
                    deleteLastStroke();
                else if (!StringUtils.isEmpty(inputText))
                    delWord();
                break;
            case R.id.bt_comma:
                enterWord(",");
                break;
            case R.id.bt_period:
                enterWord(".");
                break;
            case R.id.bt_space:
                enterWord(" ");
                break;
            case R.id.bt_num:
                if (isNumKeyboard)
                    num.setTextColor(ContextCompat.getColor(this, R.color.black));
                else
                    num.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                clearStrokeArea();
                getPresenter().changeNumKeyboard();
                isNumKeyboard = !isNumKeyboard;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_user) {
            Intent intent = new Intent(this, UserActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_setting) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}