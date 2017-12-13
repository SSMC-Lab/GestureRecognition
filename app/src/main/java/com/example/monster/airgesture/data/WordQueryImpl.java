package com.example.monster.airgesture.data;

import java.io.*;
import java.util.*;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.monster.airgesture.data.bean.CandidateWord;
import com.example.monster.airgesture.data.bean.ContactedWord;
import com.example.monster.airgesture.data.bean.ProbCode;
import com.example.monster.airgesture.data.bean.Word;
import com.example.monster.airgesture.ui.base.BaseApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * DictionaryDB的实现类，负责和数据库的创建和查询
 * Created by WelkinShadow on 2017/10/27.
 */

public class WordQueryImpl implements WordQuery {

    private Context mContext;
    private SQLiteDatabase dictionary;
    private SQLiteDatabase contacted;

    private static final String TAG = "WordQueryImpl";

    private static final String CREATE_SEQ = "create table if not exists seq (id INTEGER primary key autoincrement, strokes varchar(255), bayesProb varchar(255))";
    private static final String CREATE_RESULT = "create table if not exists result (word TEXT(120), probability DOUBLE, length INTEGER, code TEXT(120))";

    private final List<Word> candidateWords1;
    private final List<Word> candidateWords2;
    private final List<Word> candidateWords3;
    private final List<Word> candidateWords4;
    private final List<Word> candidateWords5;
    private final List<Word> candidateWords6;
    private final List<Word> num;

    private double[][] probMatrix;

    public WordQueryImpl() {
        this.mContext = BaseApplication.getContext();

        //初始化变量
        candidateWords1 = createLetters(new String[]{"I", "T", "Z", "J"}, "1");
        candidateWords2 = createLetters(new String[]{"E", "F", "H", "K", "L"}, "2");
        candidateWords3 = createLetters(new String[]{"a", "M", "N"}, "3");
        candidateWords4 = createLetters(new String[]{"V", "W", "X", "Y"}, "4");
        candidateWords5 = createLetters(new String[]{"C", "G", "O", "Q", "S", "U"}, "5");
        candidateWords6 = createLetters(new String[]{"B", "D", "P", "R"}, "6");
        num = createNumList();

        //初始化数据库
        DatabaseManager manager = DatabaseManager.getmInstance();
        dictionary = manager.getDatabase(DatabaseConfig.DB_NAME_DICTIONARY);
        contacted = manager.getDatabase(DatabaseConfig.DB_NAME_CONTACTED);
    }

    /**
     * @see WordQuery#getWords(String)
     */
    @Override
    public List<Word> getWords(String seq) {
        Log.i(TAG, "query database");
        List<Word> result;
        if (seq.length() > 1) {
            result = query(getProbCodes(seq), seq);
        } else {
            result = getLetter(seq);
        }
        return result;
    }


    /**
     * 计算出所有误识别手势的可能的纠错序列组合
     */
    private List<ProbCode> getProbCodes(String seq) {

        probMatrix = new double[0][];
        try {
            probMatrix = ProbTableUtil.createProbMatrix(mContext);
        } catch (IOException e) {
            Log.e(TAG, "txt文件未找到");
            e.printStackTrace();
        }
        List<ProbCode> result = new LinkedList<>();
        double prob;

        prob = ProbTableUtil.calculateCorrectProb(seq, probMatrix);
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
     * 数据库查询所有符合序列的单词，如果原序列存在其他的纠错序列，则查询所有符合纠错序列集的单词
     * @param probCodes 包含原序列的纠错序列集
     * @param seq       原序列
     */
    private List<Word> query(List<ProbCode> probCodes, String seq) {
        if (probCodes == null) {
            Log.e(TAG, "error: probCodes is null");
            return null;
        }

        Cursor cursor;
        Log.i(TAG, "database query word");
        //如果存在可能误判的序列组合
        if (probCodes.size() > 1) {

            Log.i(TAG, "database query : create table seq,result");
            dictionary.execSQL(CREATE_SEQ);
            dictionary.execSQL(CREATE_RESULT);

            Log.i(TAG, "database query : insert into seq");
            Iterator<ProbCode> iterator = probCodes.iterator();
            ProbCode probCode;
            while (iterator.hasNext()) {
                probCode = iterator.next();
                dictionary.execSQL("insert into seq(strokes,bayesProb) values ('"
                        + probCode.getSeq() + "'," + probCode.getWrongProb() + ")");
            }

            Log.i(TAG, "database query : insert into result");
            String length = String.valueOf(seq.length());
            dictionary.execSQL("insert into result(word,probability,length,code) " +
                    "select word,probability,length,code " +
                    "from dictionary " +
                    "where substr(code,1," + length + ") in (select strokes from seq) " );
                   // +
                 //   "limit 100");

            Log.i(TAG, "database query : query table : result");
            cursor = dictionary.rawQuery("SELECT * FROM result ORDER BY length ASC,probability DESC ", null);
        } else {
            Log.i(TAG, "database query :  query table : dictionary");
            cursor = dictionary.rawQuery("SELECT * FROM dictionary WHERE code LIKE '"
                    + seq + "%' ORDER BY length ASC,probability DESC", null);
        }

        List<Word> result = createQueryResult(cursor);
        dictionary.execSQL("drop table if exists seq");
        dictionary.execSQL("drop table if exists result");
        return new ArrayList<>(result);
    }

    /**
     * 从数据读取的数据构建返回的单词表
     */
    private List<Word> createQueryResult(Cursor cursor){
        List<Word> result = new LinkedList<>();
        CandidateWord candidateWord;
        if (cursor.moveToFirst()) {
            do {
                candidateWord = new CandidateWord(cursor);
                result.add(candidateWord);
                Log.i(TAG, "database query : word = " + candidateWord.getWord() + " length = " + candidateWord.getLength()
                        + " code = " + candidateWord.getCoding() + " probability = " + candidateWord.getProbability());
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "database query : querying result length = " + result.size());
        cursor.close();
        return result;
    }


    /**
     * 返回各个type对应的字母
     * @param type 长度为1的序列
     */
    private List<Word> getLetter(String type) {
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
        Log.i(TAG, "database query contacted");
        Cursor cursor = contacted.rawQuery("SELECT * FROM gramtable WHERE word = '"
                + word + "' ORDER BY id ASC", null);
        List<Word> result = new ArrayList<>();
        ContactedWord contactedWord;
        if (cursor.moveToFirst()) {
            do {
                contactedWord = new ContactedWord(cursor);
                result.add(contactedWord);
                Log.i(TAG, "database query : word = " + contactedWord.getWord() + " frequency = "
                        + contactedWord.getFrequency() + " contacted word = " + contactedWord.getContactedWord());
            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.i(TAG, "result length = " + result.size());
        return result;
    }

    /**
     * 创建单词表
     */
    private List<Word> createLetters(String[] letters, String coding) {
        List<Word> result = new ArrayList<>();
        for (String letter : letters) {
            result.add(new CandidateWord(letter, 0, coding, 1));
        }
        return result;
    }

    /**
     * 创建数字键盘表
     */
    private List<Word> createNumList() {
        List<Word> result = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            result.add(new CandidateWord(i + "", 0, "num", 1));
        }
        return result;
    }
}
