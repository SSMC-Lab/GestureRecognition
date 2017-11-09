package com.example.monster.airgesture.model.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.monster.airgesture.utils.FileCopyUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * DictionaryDB的实现类，负责和数据库的创建和查询
 * Created by WelkinShadow on 2017/10/27.
 */

public class DatabaseQueryManagerImpl implements DatabaseQueryManager {

    private Context context;
    private SQLiteDatabase dictionary;
    private SQLiteDatabase contacted;

    private static final String TAG = "DatabaseQueryManagerImpl";

    private static final String DB_NAME_DICTIONARY = "dictionary.db";
    private static final String DB_NAME_CONTACTED = "2gram.db";
    private static final String DB_PATH = "/data/data/com.example.monster.airgesture/database";

    private final List<CandidateWord> candidateWords1;
    private final List<CandidateWord> candidateWords2;
    private final List<CandidateWord> candidateWords3;
    private final List<CandidateWord> candidateWords4;
    private final List<CandidateWord> candidateWords5;
    private final List<CandidateWord> candidateWords6;
    private final List<CandidateWord> num;

    public DatabaseQueryManagerImpl(Context context) {
        this.context = context;

        //初始化变量
        candidateWords1 = createLetters(new String[]{"I", "T", "Z", "J"}, "1");
        candidateWords2 = createLetters(new String[]{"E", "F", "H", "K", "L"}, "2");
        candidateWords3 = createLetters(new String[]{"A", "M", "N"}, "3");
        candidateWords4 = createLetters(new String[]{"V", "W", "X", "Y"}, "4");
        candidateWords5 = createLetters(new String[]{"C", "G", "O", "Q", "S", "U"}, "5");
        candidateWords6 = createLetters(new String[]{"B", "D", "P", "R"}, "6");
        num = crateNums();

        //初始化数据库
        Log.i(TAG, "database initial ");
        boolean successful;
        successful = FileCopyUtil.databaseCopy(context, DB_NAME_DICTIONARY);
        if (!successful)
            Log.e(TAG, "dictionnary copy failed!");
        successful = FileCopyUtil.databaseCopy(context, DB_NAME_CONTACTED);
        if (!successful)
            Log.e(TAG, "2gram copy failed!");
        dictionary = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME_DICTIONARY, null);
        contacted = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME_CONTACTED, null);
    }

    @Override
    public List<CandidateWord> getWordList(String coding) {
        Log.i(TAG, "database query");
        Cursor cursor = dictionary.rawQuery("SELECT * FROM dictionary WHERE code LIKE '"
                + coding + "%' ORDER BY length ASC,probability DESC", null);
        CandidateWord candidateWord = null;
        List<CandidateWord> result = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String word = cursor.getString(cursor.getColumnIndex("word"));
                Float probability = cursor.getFloat(cursor.getColumnIndex("probability"));
                Integer length = cursor.getInt(cursor.getColumnIndex("length"));
                String code = cursor.getString(cursor.getColumnIndex("code"));
                candidateWord = new CandidateWord(word, probability, code, length);
                result.add(candidateWord);
                Log.i(TAG, "database query : word = " + word + " length = " + length
                        + " code = " + code + "probability = " + probability);
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "finish querying the result length = " + result.size());
        return result;
    }

    @Override
    public List<CandidateWord> getLetter(String type) {
        Log.d(TAG, "find letters and type is " + type);
        //避免引用传递导致的bug
        List<CandidateWord> result = new ArrayList<>();
        switch (type) {
            case "1":
                result.addAll(candidateWords1);
                break;
            case "2":
                result.addAll(candidateWords2);
                break;
            case "3":
                result.addAll(candidateWords3);
                break;
            case "4":
                result.addAll(candidateWords4);
                break;
            case "5":
                result.addAll(candidateWords5);
                break;
            case "6":
                result.addAll(candidateWords6);

        }
        return result;
    }

    @Override
    public List<CandidateWord> getNum() {
        List<CandidateWord> result = new ArrayList<>();
        result.addAll(num);
        return result;
    }

    @Override
    public List<ContactedWord> getContacted(String word) {
        Log.i(TAG, "database query");
        Cursor cursor = contacted.rawQuery("SELECT * FROM 2gramtable WHERE word = \""
                + word + "\" ORDER BY id ASC", null);
        List<ContactedWord> result = new ArrayList<>();
        ContactedWord contactedWord = null;
        if (cursor.moveToFirst()) {
            do {
                Long id = cursor.getLong(cursor.getColumnIndex("id"));
                Long frequency = cursor.getLong(cursor.getColumnIndex("frequency"));
                String wordInDB = cursor.getString(cursor.getColumnIndex("word"));
                String s2gram = cursor.getString(cursor.getColumnIndex("2gram"));
                contactedWord = new ContactedWord(id, frequency, wordInDB, s2gram);
                result.add(contactedWord);
                Log.i(TAG, "database query : id = " + id + " frequency = " + frequency + " word = " + wordInDB + "2gram = " + s2gram);
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "finish querying the result length = " + result.size());
        return result;
    }

    private List<CandidateWord> createLetters(String[] letters, String coding) {
        List<CandidateWord> result = new ArrayList<>();
        for (String letter : letters) {
            result.add(new CandidateWord(letter, 0, coding, 1));
        }
        return result;
    }

    private List<CandidateWord> crateNums() {
        List<CandidateWord> result = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            result.add(new CandidateWord(i + "", 0, "num", 1));
        }
        return result;
    }
}
