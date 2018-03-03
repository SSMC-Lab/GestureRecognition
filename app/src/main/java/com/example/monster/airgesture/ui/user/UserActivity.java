package com.example.monster.airgesture.ui.user;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.monster.airgesture.Conditions;
import com.example.monster.airgesture.R;
import com.example.monster.airgesture.ui.base.BaseActivity;
import com.example.monster.airgesture.ui.input.InputActivity;

import butterknife.BindView;
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
    protected int setContentLayoutId() {
        return R.layout.activity_user;
    }

    @Override
    protected IUserListContract.Presenter setPresenter() {
        /*待改进
        该Activity不应该指定Presenter，而是作为Presenter的控制类*/
        return new UserListPresenter();
    }

    @Override
    protected void initViews() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setSupportActionBar(toolbar);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
