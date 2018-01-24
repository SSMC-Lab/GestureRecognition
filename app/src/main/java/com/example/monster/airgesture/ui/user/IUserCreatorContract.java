package com.example.monster.airgesture.ui.user;

import com.example.monster.airgesture.ui.base.IBaseContract;

import java.util.List;

/**
 * UserCreator MVP 接口规范
 * 处理创建 User 相关的功能
 * Created by WelkinShadow on 2018/1/17.
 */

public interface IUserCreatorContract {
    interface Presenter<V extends IUserCreatorContract.View> extends IBaseContract.Presenter<V>{
        /**
         * 存储选择字母的新映射关系
         * @param selectedLetter 该次操作选中字母的集合
         * @param code 对应的在数据库中的新编码
         */
        void saveLetterMapping(List<String> selectedLetter,int code);

        /**
         * 创建新的User，并根据其操作习惯生成新的单词表
         * @param userName
         */
        void createUserByHabit(String userName);
    }

    interface View extends IBaseContract.View{
        /**
         * 刷新页面
         * @param choiceLetters 除去上一轮选择后的字母后剩下的字母集
         */
        void refreshLetters(List<String> choiceLetters);

        /**
         * 创建新用户成功时的回调
         */
        void createSuccessful();

        /**
         * 创建新用户失败时的回调
         */
        void createFailed(String error);
    }
}
