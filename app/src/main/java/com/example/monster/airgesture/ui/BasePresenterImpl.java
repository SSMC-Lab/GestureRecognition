package com.example.monster.airgesture.ui;

/**
 * Created by WelkinShadow on 2017/10/26.
 */

public class BasePresenterImpl<V extends BaseContract.View> implements BaseContract.Presenter<V> {

    private V view = null;

    public V getView() {
        return view;
    }

    @Override
    public void onAttach(V view) {
        this.view = view;
    }

    @Override
    public void onDetach() {
        view = null;
    }
}
