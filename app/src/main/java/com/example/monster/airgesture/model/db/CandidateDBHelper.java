package com.example.monster.airgesture.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by WelkinShadow on 2017/10/27.
 */

class CandidateDBHelper extends SQLiteOpenHelper {

    private Context context;

    private static final String DB_PATH = "/data/data/com.example.monster.airgesture/database";
    public static final String TAG =  "CandidateDBHelper";

    public CandidateDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
