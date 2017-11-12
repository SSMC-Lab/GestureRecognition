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

    /**
     * @see WordQuery#getWordList(String)
     */
    @Override
    public List<Word> getWordList(String seq) {
        Log.i(TAG, "database query");
        List<ProbCode> probCodes = getCandidatedCode(seq);
        List<Word> result = query(probCodes,seq);
        return result;
    }

    /**
     * 计算出所有误判的可能的集合
     */
    private List<ProbCode> getCandidatedCode(String seq) {
        // TODO: 2017/11/12 通过矩阵计算出ProbCode集合
        return null;
    }

    private List<Word> query(List<ProbCode> probCodes, String seq) {
        if (probCodes == null) {
            Log.e(TAG, "error: probCodes is null");
            return null;
        }

        Cursor cursor = null;
        List<Word> result = new ArrayList<>();
        CandidateWord candidateWord = null;

        if (probCodes.size() > 1) {
            Log.i(TAG,"create table seq,result");
            dictionary.execSQL("create table seq(" +
                    "id int primary key autoincrement, " +
                    "strokes varchar(255), " +
                    "bayesProb varchar(255))");
            dictionary.execSQL("create table result (" +
                    "word TEXT(120), " +
                    "probability DOUBLE," +
                    "length INTEGER, " +
                    "code TEXT(120))");
            Log.i(TAG,"insert into seq");
            for (ProbCode probCode : probCodes) {
                dictionary.execSQL("insert into seq(strokes,bayesProb) values ('" + probCode.getSeq() + "'," + probCode.getWrongProb() + ")");
            }
            String length = seq.length() + "";
            Log.i(TAG,"insert into result");
            dictionary.execSQL("insert into result(word,probability,length,code) " +
                    "select word,probability,length,code " +
                    "from dictionary " +
                    "where substr('" + seq + "',1," + length + "+)");

            Log.i(TAG,"query table : result");
            cursor = dictionary.rawQuery("SELECT * FROM result ORDER BY length ASC,probability DESC", null);
        } else {
            Log.i(TAG,"query table : dictionary");
            cursor = dictionary.rawQuery("SELECT * FROM dictionary WHERE code LIKE '"
                    + seq + "%' ORDER BY length ASC,probability DESC", null);
        }

        if (cursor.moveToFirst()) {
            do {
                double probability = cursor.getDouble(cursor.getColumnIndex("probability"));
                int wordLength = cursor.getInt(cursor.getColumnIndex("length"));
                String word = cursor.getString(cursor.getColumnIndex("word"));
                String wordCode = cursor.getString(cursor.getColumnIndex("code"));
                candidateWord = new CandidateWord(word, probability, wordCode, wordLength);
                result.add(candidateWord);
                Log.i(TAG, "database query : word = " + word + " length = " + wordLength
                        + " code = " + seq + " probability = " + probability);
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "querying result length = " + result.size());

        dictionary.execSQL("drop table seq");
        dictionary.execSQL("drop table result");
        return result;
    }

    /**
     * @see WordQuery#getLetter(String)
     */
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

    /**
     * @see WordQuery#getNum()
     */
    @Override
    public List<Word> getNum() {
        List<Word> result = new ArrayList<>();
        result.addAll(num);
        return result;
    }

    /**
     * @see WordQuery#getContacted(String)
     */
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
