package com.example.monster.airgesture.ui.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

/**
 * Created by WelkinShadow on 2017/10/26.
 */

public abstract class BaseActivity<T extends BaseContract.Presenter>
        extends AppCompatActivity
        implements BaseContract.View {

    private ProgressDialog mProgressDialog = null;
    private T presenter;

    protected abstract T setPresenter();

    protected abstract @LayoutRes int setLayout();

    protected abstract @MenuRes int getMenuId();

    protected T getPresenter() {
        return presenter;
    }

    protected abstract void initViews();

    protected <T extends View> T findView(@IdRes int id) {
        return (T) findViewById(id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getMenuId() != 0) {
            getMenuInflater().inflate(getMenuId(), menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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
