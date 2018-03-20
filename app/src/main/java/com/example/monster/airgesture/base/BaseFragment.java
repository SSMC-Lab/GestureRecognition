package com.example.monster.airgesture.base;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import butterknife.ButterKnife;

/**
 * Fragment基本封装
 * Created by Welkinshadow on 2018/1/19.
 */

public abstract class BaseFragment<T extends IBaseContract.Presenter>
        extends Fragment implements IBaseContract.View {

    private ProgressDialog mProgressDialog = null;

    private T mPresenter;

    protected abstract T setPresenter();

    protected T getPresenter() {
        return mPresenter;
    }

    protected abstract @LayoutRes int setLayout();

    protected abstract void intiViews();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(setLayout(), container, false);
        ButterKnife.bind(this, view);
        mPresenter = setPresenter();
        mPresenter.onAttachView(this);
        intiViews();
        return view;
    }

    @Override
    public void showLoading() {
        hideLoading();
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.show();
    }

    @Override
    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.onDetachView();
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
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
