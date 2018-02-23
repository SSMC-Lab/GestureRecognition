package com.example.monster.airgesture.ui;

import com.example.monster.airgesture.ui.input.IInputContract;
import com.example.monster.airgesture.ui.input.InputPresenter;
import com.example.monster.airgesture.ui.user.IUserCreatorContract;
import com.example.monster.airgesture.ui.user.IUserListContract;
import com.example.monster.airgesture.ui.user.UserCreatorPresenter;
import com.example.monster.airgesture.ui.user.UserListPresenter;

/**
 * presenter静态工厂
 * Created by WelkinShadow on 2018/1/17.
 */

public class PresenterFactory {

    public static <V extends IInputContract.View> IInputContract.Presenter<V> getInputPresenter() {
        return new InputPresenter<V>();
    }

    public static <V extends IUserListContract.View> IUserListContract.Presenter<V> getUserListPresenter() {
        return new UserListPresenter<V>();
    }

    public static <V extends IUserCreatorContract.View> IUserCreatorContract.Presenter<V> getUserCreatorPresenter() {
        return new UserCreatorPresenter<V>();
    }
}
