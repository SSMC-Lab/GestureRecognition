package com.example.monster.airgesture.data;

import com.example.monster.airgesture.data.user.IUserDataSource;
import com.example.monster.airgesture.data.word.IWordDataSource;
import com.example.monster.airgesture.data.user.UserRepository;
import com.example.monster.airgesture.data.word.WordRepository;

/**
 * 静态工厂
 * Created by Welkinshadow on 2018/1/17.
 */

public class DataFactory {
    public static IWordDataSource getWordDAO(){
        return WordRepository.getInstance();
    }

    public static IUserDataSource getUserDAO(){
        return UserRepository.getInstance();
    }
}
