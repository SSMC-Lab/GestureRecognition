package com.example.monster.airgesture.data;

import com.example.monster.airgesture.data.database.DataRepository;
import com.example.monster.airgesture.data.user.UserRepository;
import com.example.monster.airgesture.data.word.WordRepository;

/**
 * Created by WelkinShadow on 18/2/21.
 */

public class DataProvider {
    public static DataRepository provideDataRepository() {
        return DataRepository.getInstance(UserRepository.getInstance(), WordRepository.getInstance());
    }
}
