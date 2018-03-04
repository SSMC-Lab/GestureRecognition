package com.example.monster.airgesture.data.word;

import java.io.*;
import java.util.*;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.example.monster.airgesture.data.bean.CandidateWord;
import com.example.monster.airgesture.data.bean.ContactedWord;
import com.example.monster.airgesture.data.bean.ProbCode;
import com.example.monster.airgesture.data.bean.User;
import com.example.monster.airgesture.data.bean.Word;
import com.example.monster.airgesture.data.database.DatabaseManager;
import com.example.monster.airgesture.ui.base.BaseApplication;
import com.example.monster.airgesture.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现类，负责 Word 相关数据库的操作
 * Created by WelkinShadow on 2017/10/27.
 */

public class WordRepository implements IWordDataSource {

    private static WordRepository INSTANCE = null;
    private Context mContext;
    private SQLiteDatabase mDictionary;
    private SQLiteDatabase mContacted;
    private User user;
    private final List<Word> num;
    private double[][] probMatrix;

    public static WordRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WordRepository();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private WordRepository() {
        mContext = BaseApplication.getContext();
        num = createNumList();
        //初始化数据库
        DatabaseManager manager = DatabaseManager.getInstance();
        mDictionary = manager.getDatabase(DatabaseManager.DB_NAME_DICTIONARY);
        mContacted = manager.getDatabase(DatabaseManager.DB_NAME_CONTACTED);
    }

    @Override
    public void attachUser(@NonNull User user) {
        this.user = user;
    }

    @Override
    public void detachUser() {
        this.user = null;
    }

    @Override
    public List<Word> getWords(String seq) {
        if (seq.length() > 1) {
            return query(getProbCodes(seq), seq);
        } else if (seq.length() == 1) {
            return query(seq);
        }
        return new ArrayList<>();
    }

    /**
     * 计算出所有误识别手势的可能的纠错序列组合
     */
    private List<ProbCode> getProbCodes(String seq) {
        probMatrix = new double[0][];
        try {
            probMatrix = ProbTableUtil.createProbMatrix(mContext);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<ProbCode> result = new LinkedList<>();
        double prob = ProbTableUtil.calculateCorrectProb(seq, probMatrix);
        ProbCode firstProbCode = new ProbCode(seq, prob);
        result.add(firstProbCode);
        if (!ProbTableUtil.checkStr(seq)) {
            return result;
        }
        for (int i = 0, length = seq.length(); i < length; i++) {
            switch (seq.charAt(i)) {
                case '1':
                    result.add(createProbCode(i, seq, "2", probMatrix[1][0]));
                    result.add(createProbCode(i, seq, "4", probMatrix[3][0]));
                    break;
                case '2':
                    result.add(createProbCode(i, seq, "5", probMatrix[4][1]));
                    break;
                case '6':
                    result.add(createProbCode(i, seq, "5", probMatrix[4][5]));
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    /**
     * 改写编码，创建出纠错序列，这个序列应该是原序列的可能出现误判的序列
     */
    private ProbCode createProbCode(int index, String res, String str, double probValue) {
        String seqCopy = ProbTableUtil.replaceIndex(index, res, str);
        double prob = 1;
        for (int j = 0; j < seqCopy.length(); j++) {
            if (j == index)
                prob *= probValue;
            else {
                String s = String.valueOf(seqCopy.charAt(j));
                int temp = Integer.parseInt(s);
                prob *= probMatrix[temp - 1][temp - 1];
            }
        }
        return new ProbCode(seqCopy, prob);
    }

    /**
     * 查询字母
     */
    private List<Word> query(String seq) {
        Cursor cursor = mDictionary.rawQuery("SELECT * FROM " + user.getDictionaryName() +
                        " WHERE CODE = " + seq +
                        " ORDER BY WORD", null);
        return createQueryResult(cursor);
    }

    /**
     * 查询单词
     *
     * @param probCodes 纠错序列
     * @param seq       编码序列
     * @return 查询结果集合
     */
    private List<Word> query(List<ProbCode> probCodes, String seq) {
        if (probCodes == null) {
            LogUtils.e("error: probCodes is null");
            return null;
        }
        Cursor cursor;
        if (probCodes.size() > 1) {
            //存在可能误判的序列组合
            mDictionary.execSQL("create table if not exists seq (" +
                    "id INTEGER primary key autoincrement, " +
                    "strokes varchar(255), " +
                    "bayesProb varchar(255))");
            mDictionary.execSQL("create table if not exists result (" +
                    "word TEXT(120)," +
                    "probability DOUBLE, " +
                    "length INTEGER, " +
                    "code TEXT(120))");

            Iterator<ProbCode> iterator = probCodes.iterator();
            ProbCode probCode;
            while (iterator.hasNext()) {
                probCode = iterator.next();
                mDictionary.execSQL("insert into seq(strokes,bayesProb) values ('"
                        + probCode.getSeq() + "'," + probCode.getWrongProb() + ")");
            }

            String length = String.valueOf(seq.length());
            mDictionary.execSQL("insert into result(word,probability,length,code) " +
                    "select word,probability,length,code " +
                    "from " + user.getDictionaryName() + " " +
                    "where substr(code,1," + length + ") in (select strokes from seq) ");
            cursor = mDictionary.rawQuery("SELECT * FROM result " +
                            "ORDER BY length ASC,probability DESC ",
                    null);
        } else {
            cursor = mDictionary.rawQuery("SELECT * FROM Dictionary WHERE code LIKE '"
                    + seq + "%' ORDER BY length ASC,probability DESC", null);
        }
        List<Word> result = createQueryResult(cursor);
        mDictionary.execSQL("drop table if exists seq");
        mDictionary.execSQL("drop table if exists result");
        return new ArrayList<>(result);
    }

    /**
     * 从数据读取的数据构建返回的单词表
     */
    private List<Word> createQueryResult(Cursor cursor) {
        List<Word> result = new LinkedList<>();
        if (cursor.moveToFirst()) {
            do {
                result.add(new CandidateWord(cursor));
                /*LogUtils.d("database query :" +
                        " word = " + candidateWord.getWord() +
                        " length = " + candidateWord.getLength() +
                        " code = " + candidateWord.getCoding() +
                        " probability = " + candidateWord.getProbability());*/
            } while (cursor.moveToNext());
        }
        LogUtils.i("database query : querying result length = " + result.size());
        cursor.close();
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
        LogUtils.i("database query mContacted");
        Cursor cursor = mContacted.rawQuery(
                "SELECT * FROM gramtable " +
                        "WHERE word = '" + word + "' " +
                        "ORDER BY id ASC", null);
        List<Word> result = new ArrayList<>();
        ContactedWord contactedWord;
        if (cursor.moveToFirst()) {
            do {
                contactedWord = new ContactedWord(cursor);
                result.add(contactedWord);
                /*LogUtils.d("database query :" +
                        " word = " + contactedWord.getWord() +
                        " frequency = " + contactedWord.getFrequency() +
                        " mContacted word = " + contactedWord.getContactedWord());*/
            } while (cursor.moveToNext());
        }
        cursor.close();
        LogUtils.i("result length = " + result.size());
        return result;
    }

    /**
     * 创建数字键盘表
     */
    private List<Word> createNumList() {
        List<Word> result = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            result.add(new CandidateWord(String.valueOf(i), 0, "num", 1));
        }
        return result;
    }
}
