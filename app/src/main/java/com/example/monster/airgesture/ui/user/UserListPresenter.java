package com.example.monster.airgesture.ui.user;

import com.example.monster.airgesture.data.DataFactory;
import com.example.monster.airgesture.data.IUserDAO;
import com.example.monster.airgesture.data.bean.User;
import com.example.monster.airgesture.data.database.UserDAO;
import com.example.monster.airgesture.ui.base.BasePresenter;

import java.util.List;

/**
 * Presenter实现类 {@link IUserListContract.Presenter}
 * Created by Welkinshadow on 2018/1/18.
 */

public class UserListPresenter<V extends IUserListContract.View> extends BasePresenter<V> implements IUserListContract.Presenter<V> {

    private IUserDAO userDAO;

    public UserListPresenter() {
        userDAO = DataFactory.getUserDAO();
    }

    @Override
    public void refreshUserList() {
        getView().showAllUsers(userDAO.findAllUsers());
    }

    @Override
    public void deleteUser(User user) {
        userDAO.deleteUser(user);
        getView().showAllUsers(userDAO.findAllUsers());
    }

    @Override
    public void setCurrentUser(User user) {
        userDAO.setCurrentUser(user);
    }

    @Override
    public User getCurrentUser() {
        return userDAO.getCurrentUser();
    }
}
