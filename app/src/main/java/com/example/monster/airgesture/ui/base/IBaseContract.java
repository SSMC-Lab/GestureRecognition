package com.example.monster.airgesture.ui.base;

import android.support.annotation.StringRes;

/**
 * Created by WelkinShadow on 2017/10/26.
 */

public interface IBaseContract {

    interface Presenter<V extends View> {

        void onAttachView(V view);

        void onDetachView();
    }

    interface View {

        void showLoading();

        void hideLoading();

        void onError(@StringRes int resId);

        void onError(String message);

        void showMessage(String message);
    }
}
