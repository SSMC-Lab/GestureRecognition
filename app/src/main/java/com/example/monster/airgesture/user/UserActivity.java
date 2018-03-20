package com.example.monster.airgesture.user;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.monster.airgesture.GlobalConfig;
import com.example.monster.airgesture.R;
import com.example.monster.airgesture.input.InputActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserActivity extends AppCompatActivity
        implements UserListFragment.OnFragmentInteractionListener,
        UserCreatorFragment.OnFragmentInteractionListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);
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
        if (uri.toString().equals(GlobalConfig.URI_INTERACTION_SUBMIT)) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_user, UserListFragment.newInstance());
            transaction.commit();
        } else if (uri.toString().equals(GlobalConfig.URI_INTERACTION_SELECT_USER)) {
            Intent intent = new Intent(this, InputActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
