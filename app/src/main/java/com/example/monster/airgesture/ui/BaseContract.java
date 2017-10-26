package com.example.monster.airgesture.ui;

import android.support.annotation.StringRes;

/**
 * Created by WelkinShadow on 2017/10/26.
 */

public interface BaseContract {

    interface Presenter<V extends View> {

        void onAttach(V view);

        void onDetach();
    }

    interface View {

        void showLoading();

        void hideLoading();

        void closeActivity();

        void onError(@StringRes int resId);

        void onError(String message);

        void showMessage(String message);
    }
}
