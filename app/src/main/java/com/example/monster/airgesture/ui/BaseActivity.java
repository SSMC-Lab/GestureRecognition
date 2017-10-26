package com.example.monster.airgesture.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

/**
 * Created by WelkinShadow on 2017/10/26.
 */

public abstract class BaseActivity<T extends BaseContract.Presenter>
        extends ActionBarActivity
        implements BaseContract.View {

    private ProgressDialog mProgressDialog = null;
    private T presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
        initialize();
        presenter = setPresenter();
        presenter.onAttach(this);
    }

    @Override
    public void showLoading() {
        hideLoading();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.show();
    }

    @Override
    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    @Override
    public void closeActivity() {
        finish();
    }

    @Override
    public void onError(@StringRes int resId) {
        onError(getString(resId));
    }

    @Override
    public void onError(String message) {
        showMessage(message);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    public abstract T setPresenter();

    public abstract
    @LayoutRes
    int setLayout();

    public T getPresenter() {
        return presenter;
    }

    public abstract void initialize();
}
