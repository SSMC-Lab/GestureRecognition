package com.example.monster.airgesture.ui.user;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.monster.airgesture.Conditions;
import com.example.monster.airgesture.R;
import com.example.monster.airgesture.ui.PresenterFactory;
import com.example.monster.airgesture.ui.base.BaseFragment;
import com.example.monster.airgesture.utils.StringUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * MVP 中的 View 层 {@link IUserCreatorContract}
 * Created by Welkinshadow on 2018/1/19.
 */

public class UserCreatorFragment extends BaseFragment<IUserCreatorContract.Presenter>
        implements IUserCreatorContract.View {

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.recyclerView_letter)
    RecyclerView recyclerView;
    @BindView(R.id.bt_user_creator)
    Button btNextStep;
    @BindView(R.id.text_hint_gesture)
    TextView hint;
    @BindView(R.id.edit_user_name)
    EditText userName;

    private Queue<Task> taskQueue = new ArrayDeque<>();//待执行的任务队列

    private LetterAdapter mAdapter;
    private List<String> mSelectedLetter = new ArrayList<>();
    private List<String> mChoicedLetter = new ArrayList<>(Arrays.asList(
            "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"));
    private LetterAdapter.OnItemClickListener onItemClickListener = new LetterAdapter.OnItemClickListener() {
        @Override
        public void onClickItem(String letter, LetterAdapter.ViewHolder holder) {
            if (holder.isSelected()) {
                mSelectedLetter.remove(letter);
                holder.textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
            } else {
                mSelectedLetter.add(letter);
                holder.textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.blue));
            }
            holder.setSelected(!holder.isSelected());
        }

        @Override
        public void onLongClickItem(String letter, LetterAdapter.ViewHolder holder) {

        }
    };

    public static UserCreatorFragment newInstance() {
        return new UserCreatorFragment();
    }

    @Override
    protected IUserCreatorContract.Presenter setPresenter() {
        return PresenterFactory.getUserCreatorPresenter();
    }

    @Override
    protected int setLayout() {
        return R.layout.fragment_user_creator;
    }

    @Override
    protected void intiViews() {
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        btNextStep.setText(R.string.user_creator_next);
        refreshLettersAfterSelected(mChoicedLetter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        taskQueue.add(new Task("当您写 一 的时候，您是想写……", Task.TASK_TYPE.NEXT_LETTER_SELECTED,
                Conditions.gestureHengCode));
        taskQueue.add(new Task("当您写 | 的时候，您是想写……", Task.TASK_TYPE.NEXT_LETTER_SELECTED,
                Conditions.gestureShuCode));
        taskQueue.add(new Task("当您写 / 的时候，您是想写……", Task.TASK_TYPE.NEXT_LETTER_SELECTED,
                Conditions.gestureZuoXieCode));
        taskQueue.add(new Task("当您写 \\ 的时候，您是想写……", Task.TASK_TYPE.NEXT_LETTER_SELECTED,
                Conditions.gestureYouXieCode));
        taskQueue.add(new Task("当您写 ⊂ 的时候，您是想写……", Task.TASK_TYPE.NEXT_LETTER_SELECTED,
                Conditions.gestureZuoHuCode));
        taskQueue.add(new Task("当您写 ⊃ 的时候，您是想写……\n点击提交完成用户创建,请确认您填写了用户名",
                Task.TASK_TYPE.CREATE_USER, -1));
        showNextTask();
    }

    @Override
    public void refreshLettersAfterSelected(List<String> choiceLetters) {
        if (mAdapter == null) {
            mAdapter = new LetterAdapter(choiceLetters, onItemClickListener);
            recyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDiff(new ArrayList<String>(mChoicedLetter));
        }
    }

    @OnClick(R.id.bt_user_creator)
    void nextStep() {
        //从任务队列中查找任务
        Task task = taskQueue.poll();
        if (task != null) {
            if (task.type == Task.TASK_TYPE.CREATE_USER) {
                String username = userName.getText().toString();
                if (!StringUtils.isEmpty(username)) {
                    //右弧对应的字母的此时还没提交
                    //choiceLetter中剩下的字母都是右弧的
                    getPresenter().saveLetterMapping(mChoicedLetter, Conditions.gestureYouHuCode);
                    getPresenter().createUser(username);
                    mListener.onFragmentInteraction(
                            Uri.parse(Conditions.URI_INTERACTION_SUBMIT));
                } else {
                    Snackbar.make(getView(), "必须输入用户名", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    taskQueue.add(new Task("当您写 ⊃ 的时候，您是想写……\n" +
                            "点击提交完成用户创建,请确认您填写了用户名",
                            Task.TASK_TYPE.CREATE_USER, -1));
                    showNextTask();
                }
            } else if (task.type == Task.TASK_TYPE.NEXT_LETTER_SELECTED) {
                getPresenter().saveLetterMapping(mSelectedLetter, task.gestureCode);
                mChoicedLetter.removeAll(mSelectedLetter);
                refreshLettersAfterSelected(mChoicedLetter);
            }
        }
        mSelectedLetter.clear();
        showNextTask();
    }

    /**
     * 展示队列中任务信息
     */
    private void showNextTask() {
        Task task = taskQueue.peek();
        if (task != null) {
            hint.setText(task.hint);
            if (task.type == Task.TASK_TYPE.CREATE_USER) {
                btNextStep.setText(R.string.user_creator_submit);
            } else if (task.type == Task.TASK_TYPE.NEXT_LETTER_SELECTED) {
                btNextStep.setText(R.string.user_creator_submit);
            }
        }
    }

    @Override
    public void createSuccessful() {
        Snackbar.make(getView(), "成功创建用户", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    @Override
    public void createFailed(String error) {
        Snackbar.make(getView(), error, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserListFragment.OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     * 任务队列中的任务
     */
    static class Task {

        enum TASK_TYPE {
            NEXT_LETTER_SELECTED,
            CREATE_USER
        }

        private String hint;//用于提示用户操作
        private TASK_TYPE type;//用于识别当前任务类型
        private int gestureCode;//用于识别手势习惯设定时的编码对照，当type为CREATE_USER不起作用

        Task(String hint, TASK_TYPE type, int gestureCode) {
            this.hint = hint;
            this.type = type;
            this.gestureCode = gestureCode;
        }
    }
}
