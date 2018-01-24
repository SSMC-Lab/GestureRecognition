package com.example.monster.airgesture.data;

import com.example.monster.airgesture.data.database.UserDAO;
import com.example.monster.airgesture.data.database.WordDAO;

/**
 * 静态工厂
 * Created by Welkinshadow on 2018/1/17.
 */

public class DataFactory {
    public static IWordDAO getWordDAO(){
        return new WordDAO();
    }

    public static IUserDAO getUserDAO(){
        return new UserDAO();
    }
}
