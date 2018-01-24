package com.example.monster.airgesture.ui.user;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.monster.airgesture.Conditions;
import com.example.monster.airgesture.R;
import com.example.monster.airgesture.ui.PresenterFactory;
import com.example.monster.airgesture.ui.base.BaseActivity;
import com.example.monster.airgesture.ui.input.InputActivity;
import com.example.monster.airgesture.ui.test.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserActivity extends BaseActivity<IUserListContract.Presenter>
        implements UserListFragment.OnFragmentInteractionListener,
        UserCreatorFragment.OnFragmentInteractionListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private FragmentManager fragmentManager;

    @Override
    protected IUserListContract.Presenter setPresenter() {
        ///这里不该传一个Presenter实例，待改进
        return PresenterFactory.getUserListPresenter();
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_user;
    }

    @Override
    protected int getMenuId() {
        return R.menu.menu_main;
    }

    @Override
    protected void initViews() {
        ButterKnife.bind(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_user, UserListFragment.newInstance());
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.fab)
    void clickFab() {
        fab.setVisibility(View.GONE);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_user, UserCreatorFragment.newInstance());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //回调从fragment传来的数据
        if (uri.toString().equals(Conditions.URI_INTERACTION_SUBMIT) ) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_user, UserListFragment.newInstance());
            transaction.commit();
        } else if (uri.toString().equals(Conditions.URI_INTERACTION_SELECT_USER)) {
            Intent intent = new Intent(this, InputActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_main) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
