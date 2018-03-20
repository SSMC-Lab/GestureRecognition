package com.example.monster.airgesture.user;

import com.example.monster.airgesture.data.DataProvider;
import com.example.monster.airgesture.data.IDataSource;
import com.example.monster.airgesture.data.bean.User;
import com.example.monster.airgesture.base.BasePresenter;

/**
 * Presenter实现类 {@link IUserListContract.Presenter}
 * Created by Welkinshadow on 2018/1/18.
 */

public class UserListPresenter extends BasePresenter<IUserListContract.View>
        implements IUserListContract.Presenter<IUserListContract.View> {

    private IDataSource dataRepository;

    public UserListPresenter() {
        dataRepository = DataProvider.provideDataRepository();
    }

    @Override
    public void refreshUserList() {
        getView().showAllUsers(dataRepository.findAllUsers());
    }

    @Override
    public void deleteUser(User user) {
        dataRepository.deleteUser(user);
        getView().showAllUsers(dataRepository.findAllUsers());
    }

    @Override
    public void setCurrentUser(User user) {
        dataRepository.setCurrentUser(user);
    }

    @Override
    public User getCurrentUser() {
        return dataRepository.getCurrentUser();
    }
}
