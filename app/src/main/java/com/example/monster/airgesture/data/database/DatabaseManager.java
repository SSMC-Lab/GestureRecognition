package com.example.monster.airgesture.data.database;

import android.database.sqlite.SQLiteDatabase;
import com.example.monster.airgesture.utils.FileCopyUtils;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库管理
 * Created by WelkinShadow on 2017/11/15.
 */

public class DatabaseManager {

    private static DatabaseManager mInstance;

    private Map<String, SQLiteDatabase> databases = new HashMap<>();

    private DatabaseManager() {
        try {
            createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回单例
     */
    public static DatabaseManager getmInstance() {
        if (mInstance == null) {
            mInstance = new DatabaseManager();
        }
        return mInstance;
    }

    /**
     * 创建数据库
     */
    private void createDataBase() throws IOException {
        if (!checkDatabase(DatabaseConfig.DB_NAME_DICTIONARY)) {
            copyDatabase(DatabaseConfig.DB_NAME_DICTIONARY);
        }
        if (!checkDatabase(DatabaseConfig.DB_NAME_CONTACTED)) {
            copyDatabase(DatabaseConfig.DB_NAME_CONTACTED);
        }
        databases.put(DatabaseConfig.DB_NAME_DICTIONARY, SQLiteDatabase.openOrCreateDatabase(DatabaseConfig.DB_PATH + DatabaseConfig.DB_NAME_DICTIONARY, null));
        databases.put(DatabaseConfig.DB_NAME_CONTACTED, SQLiteDatabase.openOrCreateDatabase(DatabaseConfig.DB_PATH + DatabaseConfig.DB_NAME_CONTACTED, null));
    }

    /**
     * 检查数据库是否存在
     * @param dbName 数据库在database目录下的文件名
     */
    private boolean checkDatabase(String dbName) {
        File dbFile = new File(DatabaseConfig.DB_PATH + dbName);
        return dbFile.exists();
    }

    /**
     * 从assets目录下拷贝数据库
     * @param dbName 数据库在assets目录下的文件名
     */
    private void copyDatabase(String dbName) throws IOException {
        FileCopyUtils.copyInAssets(dbName,DatabaseConfig.DB_PATH + dbName);
    }

    /**
     * 依据键值对获取database实例
     */
    public SQLiteDatabase getDatabase(String dbName) {
        return databases.get(dbName);
    }
}
