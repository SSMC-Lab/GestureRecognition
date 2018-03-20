package com.example.monster.airgesture.user;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.monster.airgesture.GlobalConfig;
import com.example.monster.airgesture.R;
import com.example.monster.airgesture.data.bean.User;
import com.example.monster.airgesture.base.BaseFragment;

import java.util.List;

import butterknife.BindView;

/**
 * MVP中的View层 {@link IUserListContract}
 * Created by Welkinshadow on 2018/1/18.
 */
public class UserListFragment extends BaseFragment<IUserListContract.Presenter> implements IUserListContract.View {

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.user_recyclerview)
    RecyclerView usersRecyclerView;

    private UserAdapter userAdapter;

    private UserAdapter.OnItemClickListener onItemClickListener = new UserAdapter.OnItemClickListener() {
        @Override
        public void onClickItem(User user) {
            getPresenter().setCurrentUser(user);
            mListener.onFragmentInteraction(Uri.parse(GlobalConfig.URI_INTERACTION_SELECT_USER));
        }

        @Override
        public void onLongClickItem(User user) {
            getPresenter().deleteUser(user);
        }
    };

    public static UserListFragment newInstance(){
        return new UserListFragment();
    }

    @Override
    protected IUserListContract.Presenter setPresenter() {
        return new UserListPresenter();
    }

    @Override
    protected int setLayout() {
        return R.layout.fragment_user_list;
    }

    @Override
    protected void intiViews() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        usersRecyclerView.setLayoutManager(manager);
        getPresenter().refreshUserList();
    }

    @Override
    public void showAllUsers(List<User> users) {
        //展示由presenter回传的数据
        if (userAdapter == null) {
            userAdapter = new UserAdapter(users, onItemClickListener);
            usersRecyclerView.setAdapter(userAdapter);
        } else {
            userAdapter.notifyDiff(users);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
