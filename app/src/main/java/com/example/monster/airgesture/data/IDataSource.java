package com.example.monster.airgesture.data;

import com.example.monster.airgesture.data.bean.User;
import com.example.monster.airgesture.data.bean.Word;

import java.util.List;

/**
 * Created by WelkinShadow on 18/2/21.
 */

public interface IDataSource {
    interface OnUserCreateListener {
        void OnStart();

        void OnSuccessful();

        void OnFailed();
    }

    List<Word> findWords(String coding);

    List<Word> findContactedWord(String word);

    List<Word> getNum();

    User getCurrentUser();

    void setCurrentUser(User user);

    void resetCurrentUser();

    void createDictionaryByHabit(User user, OnUserCreateListener listener);

    List<User> findAllUsers();

    void saveUser(User user);

    void deleteUser(User user);

    void updateUser(User user);

    User getDefaultUser();
}
