package com.example.monster.airgesture.data;

import com.example.monster.airgesture.data.bean.User;

import java.util.List;

/**
 * User数据库访问控制接口
 * Created by Welkinshadow on 2018/1/17.
 */

public interface IUserDAO {

    interface OnCreateListener {

        void OnStart();

        void OnSuccessful();

        void OnFailed();
    }

    /** 根据用户习惯创建dictionary */
    void createDictionaryByHabit(User user,OnCreateListener listener);

    /** 获取当前用户 */
    User getCurrentUser();

    /** 设置当前用户 */
    void setCurrentUser(User user);

    /** 查找所有注册用户 */
    List<User> findAllUsers();

    User findUserById(int id);

    void saveUser(User user);

    void deleteUser(User user);

    void deleteUser(int id);

    void updateUser(User user);

    User getDefaultUser();
}
