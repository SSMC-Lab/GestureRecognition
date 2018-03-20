package com.example.monster.airgesture.base;

/**
 * Created by WelkinShadow on 2017/10/26.
 */

public class BasePresenter<V extends IBaseContract.View> implements IBaseContract.Presenter<V> {

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
