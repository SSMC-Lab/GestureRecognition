package com.example.monster.airgesture.ui.input;

import com.example.monster.airgesture.data.bean.Word;
import com.example.monster.airgesture.ui.base.IBaseContract;

import java.util.List;

/**
 * MVP接口规范，该接口规定了主界面/输入界面的执行接口
 * Created by WelkinShadow on 2017/10/26.
 */

public interface IInputContract {

    interface Presenter<V extends View> extends IBaseContract.Presenter<V>{

        /**
         * 根据单词查找相关的关联词
         * @param word
         */
        void findContactedWord(String word);

        /**
         * 传递在键盘和单词输入状态的切换
         */
        void changeNumKeyboard();

        /**
         * 刷新当前选择的用户以提供不同的序列查找表
         */
        void resetCurrentUser();

        /**
         * 开启手势识别功能
         */
        void startRecording();

        /**
         * 关闭手势识别功能
         */
        void stopRecording();

        /**
         * 清空所有笔画
         */
        void clearStoker();

        /**
         * 删除笔画的最后一位
         */
        void delStoker();
    }

    interface View extends IBaseContract.View{

        /**
         * 把从后台接受到的手势识别的结果显示在view层
         * 手势识别的结果是一个int型{@link com.example.monster.airgesture.Conditions#STOKES}
         * @param stokes
         */
        void enterStroke(int stokes);

        /**
         * 将查找到的单词显示在view层
         * @param candidateWord 从数据库查到的单词集
         */
        void showWordInWordArea(List<Word> candidateWord);
    }
}
