package com.example.monster.airgesture.data.database;

import com.example.monster.airgesture.data.IDataSource;
import com.example.monster.airgesture.data.bean.User;
import com.example.monster.airgesture.data.bean.Word;
import com.example.monster.airgesture.data.user.IUserDataSource;
import com.example.monster.airgesture.data.user.UserRepository;
import com.example.monster.airgesture.data.word.IWordDataSource;
import com.example.monster.airgesture.data.word.WordRepository;

import java.util.List;

/**
 * Created by WelkinShadow on 18/2/21.
 */

public class DataRepository implements IDataSource {

    private static DataRepository INSTANCE = null;

    private IUserDataSource mUserRepository;
    private IWordDataSource mWordRepository;

    private DataRepository(UserRepository mUserRepository, WordRepository mWordRepository) {
        this.mUserRepository = mUserRepository;
        this.mWordRepository = mWordRepository;
    }

    public static DataRepository getInstance(UserRepository mUserRepository, WordRepository mWordRepository) {
        if (INSTANCE == null) {
            INSTANCE = new DataRepository(mUserRepository, mWordRepository);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public List<Word> findWords(String coding) {
        return mWordRepository.getWords(coding);
    }

    @Override
    public List<Word> findContactedWord(String word) {
        return mWordRepository.getContacted(word);
    }

    @Override
    public List<Word> getNum() {
        return mWordRepository.getNum();
    }

    @Override
    public User getCurrentUser() {
        return mUserRepository.getCurrentUser();
    }

    @Override
    public void setCurrentUser(User user) {
        mUserRepository.setCurrentUser(user);
        mWordRepository.attachUser(user);
    }

    @Override
    public void resetCurrentUser() {
        mUserRepository.setCurrentUser(mUserRepository.getDefaultUser());
    }

    @Override
    public void createDictionaryByHabit(User user, final OnUserCreateListener listener) {
        mUserRepository.createDictionaryByHabit(user, new IUserDataSource.OnCreateListener() {
            @Override
            public void OnStart() {
                listener.OnStart();
            }

            @Override
            public void OnSuccessful() {
                listener.OnSuccessful();
            }

            @Override
            public void OnFailed() {
                listener.OnFailed();
            }
        });
    }

    @Override
    public List<User> findAllUsers() {
        return mUserRepository.findAllUsers();
    }

    @Override
    public void saveUser(User user) {
        mUserRepository.saveUser(user);
    }

    @Override
    public void deleteUser(User user) {
        mUserRepository.deleteUser(user);
    }

    @Override
    public void updateUser(User user) {
        mUserRepository.deleteUser(user);
    }

    @Override
    public User getDefaultUser() {
        return mUserRepository.getDefaultUser();
    }
}
