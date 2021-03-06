package com.example.monster.airgesture.data;

import com.example.monster.airgesture.data.database.DataRepository;
import com.example.monster.airgesture.data.user.UserRepository;
import com.example.monster.airgesture.data.word.WordRepository;

/**
 * 依赖注入
 * Created by WelkinShadow on 18/2/21.
 */

public class DataProvider {
    public static IDataSource provideDataRepository() {
        return DataRepository.getInstance(UserRepository.getInstance(), WordRepository.getInstance());
    }
}
