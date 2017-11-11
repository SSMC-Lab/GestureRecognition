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

public class WordQueryImpl implements WordQuery {

    private Context context;
    private SQLiteDatabase dictionary;
    private SQLiteDatabase contacted;

    private static final String TAG = "WordQueryImpl";

    private static final String DB_NAME_DICTIONARY = "dictionary.db";
    private static final String DB_NAME_CONTACTED = "2gram.db";
    private static final String DB_PATH = "/data/data/com.example.monster.airgesture/database";

    private final List<Word> candidateWords1;
    private final List<Word> candidateWords2;
    private final List<Word> candidateWords3;
    private final List<Word> candidateWords4;
    private final List<Word> candidateWords5;
    private final List<Word> candidateWords6;
    private final List<Word> num;

    public WordQueryImpl(Context context) {
        this.context = context;

        //初始化变量
        candidateWords1 = createLetters(new String[]{"i", "t", "z", "j"}, "1");
        candidateWords2 = createLetters(new String[]{"e", "f", "h", "k", "l"}, "2");
        candidateWords3 = createLetters(new String[]{"a", "m", "n"}, "3");
        candidateWords4 = createLetters(new String[]{"v", "w", "x", "y"}, "4");
        candidateWords5 = createLetters(new String[]{"c", "g", "o", "q", "s", "u"}, "5");
        candidateWords6 = createLetters(new String[]{"b", "d", "p", "r"}, "6");
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
    public List<Word> getWordList(String coding) {
        Log.i(TAG, "database query");
        Cursor cursor = dictionary.rawQuery("SELECT * FROM dictionary WHERE code LIKE '"
                + coding + "%' ORDER BY length ASC,probability DESC", null);
        CandidateWord candidateWord = null;
        List<Word> result = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                double probability = cursor.getDouble(cursor.getColumnIndex("probability"));
                int length = cursor.getInt(cursor.getColumnIndex("length"));
                String word = cursor.getString(cursor.getColumnIndex("word"));
                String code = cursor.getString(cursor.getColumnIndex("code"));
                candidateWord = new CandidateWord(word, probability, code, length);
                result.add(candidateWord);
                Log.i(TAG, "database query : word = " + word + " length = " + length
                        + " code = " + code + " probability = " + probability);
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "querying result length = " + result.size());
        return result;
    }

    @Override
    public List<Word> getLetter(String type) {
        Log.d(TAG, "find letters and type is " + type);
        //避免引用传递导致的bug
        List<Word> result = new ArrayList<>();
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
    public List<Word> getNum() {
        List<Word> result = new ArrayList<>();
        result.addAll(num);
        return result;
    }

    @Override
    public List<Word> getContacted(String word) {
        Log.i(TAG, "database query");
        Cursor cursor = contacted.rawQuery("SELECT * FROM gramtable WHERE word = '"
                + word + "' ORDER BY id ASC", null);
        List<Word> result = new ArrayList<>();
        ContactedWord contactedWord = null;
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex("id"));
                long frequency = cursor.getLong(cursor.getColumnIndex("frequency"));
                String wordInDB = cursor.getString(cursor.getColumnIndex("word"));
                String s2gram = cursor.getString(cursor.getColumnIndex("2gram"));
                contactedWord = new ContactedWord(id, frequency, s2gram, wordInDB);
                result.add(contactedWord);
                Log.i(TAG, "database query : id = " + id + " frequency = " + frequency + " word = " + wordInDB + " 2gram = " + s2gram);
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "result length = " + result.size());
        return result;
    }

    private List<Word> createLetters(String[] letters, String coding) {
        List<Word> result = new ArrayList<>();
        for (String letter : letters) {
            result.add(new CandidateWord(letter, 0, coding, 1));
        }
        return result;
    }

    private List<Word> crateNums() {
        List<Word> result = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            result.add(new CandidateWord(i + "", 0, "num", 1));
        }
        return result;
    }
}
