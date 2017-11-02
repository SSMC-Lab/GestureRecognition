package com.example.monster.airgesture.model.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.monster.airgesture.utils.FileCopyUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by WelkinShadow on 2017/10/27.
 */

public class DictionaryDBImpl implements DictionaryDB {

    private Context context;
    private SQLiteDatabase database;
    private static final String TAG = "DictionaryDBImpl";
    private static final String DB_NAME = "dictionary.db";
    private static final String DB_PATH = "/data/data/com.example.monster.airgesture/database";

    private final List<CandidateWord> candidateWords1;
    private final List<CandidateWord> candidateWords2;
    private final List<CandidateWord> candidateWords3;
    private final List<CandidateWord> candidateWords4;
    private final List<CandidateWord> candidateWords5;
    private final List<CandidateWord> candidateWords6;
    private final List<CandidateWord> num;

    public DictionaryDBImpl(Context context) {
        candidateWords1 = createLetters(new String[]{"I", "T", "Z", "J"}, "1");
        candidateWords2 = createLetters(new String[]{"E", "F", "H", "K", "L"}, "2");
        candidateWords3 = createLetters(new String[]{"A", "M", "N"}, "3");
        candidateWords4 = createLetters(new String[]{"V", "W", "X", "Y"}, "4");
        candidateWords5 = createLetters(new String[]{"C", "G", "O", "Q", "S", "U"}, "5");
        candidateWords6 = createLetters(new String[]{"B", "D", "P", "R"}, "6");
        num = crateNums();

        Log.i(TAG, "database initial ");
        this.context = context;
        File dbFile = new File(DB_PATH + DB_NAME);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        if (!dbFile.exists()) {
            Log.i(TAG, "copy database from assets");
            try {
                dbFile.createNewFile();
                inputStream = context.getAssets().open(DB_NAME);
                outputStream = new FileOutputStream(dbFile);
                boolean result = FileCopyUtil.copy(inputStream, outputStream);
                if (!result) {
                    Log.e(TAG, "fail to copy database");
                }
                Log.i(TAG, "copy right");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        database = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
    }

    @Override
    public List<CandidateWord> getWordList(String coding) {
        Log.i(TAG, "database query");
        Cursor cursor = database.rawQuery("SELECT * FROM dictionary WHERE code LIKE '"
                + coding + "%' ORDER BY length ASC,probability DESC", null);
        CandidateWord candidateWord = null;
        List<CandidateWord> result = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String word = cursor.getString(cursor.getColumnIndex("word"));
                float probability = cursor.getFloat(cursor.getColumnIndex("probability"));
                int length = cursor.getInt(cursor.getColumnIndex("length"));
                String code = cursor.getString(cursor.getColumnIndex("code"));
                candidateWord = new CandidateWord(word, probability, code, length);
                result.add(candidateWord);
                Log.d(TAG, "database query : word = " + word + " length = " + length
                        + " code = " + code + "probability = " + probability);
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "finish querying the result length = " + result.size());
        return result;
    }

    @Override
    public List<CandidateWord> getLetter(String type) {
        Log.d(TAG, "find letters and type is " + type);
        //避免直接传递类变量成员
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

    private static List<CandidateWord> createLetters(String[] letters, String coding) {
        List<CandidateWord> result = new ArrayList<>();
        CandidateWord word;
        for (String letter : letters) {
            word = new CandidateWord(letter, 0, coding, 1);
            result.add(word);
        }
        return result;
    }

    private static List<CandidateWord> crateNums() {
        List<CandidateWord> result = new ArrayList<>();
        CandidateWord word;
        for (int i = 0; i <= 10; i++) {
            word = new CandidateWord(i + "", 0, "num", 1);
            result.add(word);
        }
        return result;
    }
}
