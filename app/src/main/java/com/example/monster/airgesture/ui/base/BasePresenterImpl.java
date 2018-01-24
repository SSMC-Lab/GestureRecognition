package com.example.monster.airgesture.ui.base;

/**
 * Created by WelkinShadow on 2017/10/26.
 */

public class BasePresenterImpl<V extends IBaseContract.View> implements IBaseContract.Presenter<V> {

    private V view = null;

    public V getView() {
        return view;
    }

    @Override
    public void onAttachView(V view) {
        this.view = view;
    }

    @Override
    public void onDetachView() {
        view = null;
    }
}
