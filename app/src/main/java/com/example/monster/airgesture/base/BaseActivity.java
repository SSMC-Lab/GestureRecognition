package com.example.monster.airgesture.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import butterknife.ButterKnife;

/**
 * Created by WelkinShadow on 2017/10/26.
 */

public abstract class BaseActivity<T extends IBaseContract.Presenter>
        extends AppCompatActivity
        implements IBaseContract.View {

    private ProgressDialog mProgressDialog = null;
    private T presenter;

    protected abstract @LayoutRes
    int setContentLayoutId();

    protected abstract T setPresenter();

    protected T getPresenter() {
        return presenter;
    }

    protected abstract void initViews();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setContentLayoutId());
        ButterKnife.bind(this);
        presenter = setPresenter();
        presenter.onAttachView(this);
        initViews();
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
        presenter.onDetachView();
        super.onDestroy();
    }
}
