package com.example.monster.airgesture.data.user;

import com.example.monster.airgesture.Conditions;
import com.example.monster.airgesture.data.bean.User;
import com.example.monster.airgesture.utils.SPUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 实现类，负责 User 相关数据的数据库操作
 * Created by Welkinshadow on 2018/1/20.
 */

public class UserRepository implements IUserDataSource {

    private User defaultUser;

    private static UserRepository INSTANCE = null;

    //默认的手势类型-字母映射
    private final List<Integer> defaultCode = new ArrayList<>(Arrays.asList(
            3, 6, 5, 6, 2, 2, 5,
            2, 1, 1, 2, 2, 3, 3,
            5, 6, 5, 6, 5, 1,
            5, 4, 4, 4, 4, 1));

    public static UserRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserRepository();
        }
        return INSTANCE;
    }

    private UserRepository() {
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void createDictionaryByHabit(User user, OnCreateListener listener) {
        listener.OnStart();
        saveUser(user);
        UserCreatorHelper.Companion.createNewDictionary(user, this, listener);
    }

    @Override
    public User getCurrentUser() {
        int currentUserId = (int) SPUtils.get(Conditions.CURRENT_USER_ID, -1);
        if (currentUserId != -1) {
            return DataSupport.find(User.class, currentUserId);
        } else {
            getDefaultUser().save();
            setCurrentUser(defaultUser);
            return defaultUser;
        }
    }

    @Override
    public void setCurrentUser(User user) {
        SPUtils.put(Conditions.CURRENT_USER_ID, user.getId());
    }

    @Override
    public List<User> findAllUsers() {
        List<User> result = new ArrayList<>();
        result.addAll(DataSupport.findAll(User.class));
        if (result.isEmpty()) {
            result.add(getDefaultUser());
        }
        return result;
    }

    @Override
    public User findUserById(int id) {
        return DataSupport.find(User.class, id);
    }

    @Override
    public void saveUser(User user) {
        user.save();
    }

    @Override
    public void deleteUser(User user) {
        if (getCurrentUser().getId() == user.getId()) {
            setCurrentUser(getDefaultUser());
        }
        user.delete();
    }

    @Override
    public void deleteUser(int id) {
        DataSupport.find(User.class, id).delete();
    }

    @Override
    public void updateUser(User user) {
        user.save();
    }

    @Override
    public User getDefaultUser() {
        if (defaultUser == null) {
            defaultUser = new User("dictionary", defaultCode);
        }
        return defaultUser;
    }
}
