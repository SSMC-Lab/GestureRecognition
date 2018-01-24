package com.example.monster.airgesture.ui.user;

import com.example.monster.airgesture.data.bean.User;
import com.example.monster.airgesture.ui.base.IBaseContract;

import java.util.List;


/**
 * UserCreator MVP 接口规范
 * 列出所有 User 的并提供一些操作功能
 * Created by WelkinShadow on 2018/1/17.
 */

public interface IUserListContract {
    interface Presenter<V extends IUserListContract.View> extends IBaseContract.Presenter<V>{
        /**
         * 查找数据库并将数据返回给View层
         */
        void refreshUserList();

        /**
         * 从数据库删除选中的User
         * @param user
         */
        void deleteUser(User user);

        /**
         * 设置当前的User
         * @param user
         */
        void setCurrentUser(User user);

        /**
         * 获取当前的User
         */
        User getCurrentUser();
    }

    interface View extends IBaseContract.View{
        /**
         * 展示从数据库中获取的所有User
         * @param users
         */
        void showAllUsers(List<User> users);
    }
}
