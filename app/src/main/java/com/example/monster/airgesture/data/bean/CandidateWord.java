package com.example.monster.airgesture.data.bean;

import android.database.Cursor;

/**
 * 候选的实体类封装
 * Created by WelkinShadow on 2017/10/27.
 */

public class CandidateWord extends Word {
    private String coding;
    private double probability;
    private int length;

    /**
     * 数据表字段名
     */
    public static final String TABLE_NAME = "dictionary";
    public static final String WORD = "word";
    public static final String LENGTH = "length";
    public static final String CODE = "code";
    public static final String PROBABILITY = "probability";

    public CandidateWord(String word, double probability, String coding, int length) {
        this.word = word;
        this.probability = probability;
        this.coding = coding;
        this.length = length;
    }

    public CandidateWord(Cursor cursor){
        word = cursor.getString(cursor.getColumnIndex(WORD));
        length = cursor.getInt(cursor.getColumnIndex(LENGTH));
        coding = cursor.getString(cursor.getColumnIndex(CODE));
        probability = cursor.getDouble(cursor.getColumnIndex(PROBABILITY));
    }

    public double getProbability() {
        return probability;
    }

    public String getCoding() {
        return coding;
    }

    public int getLength() {
        return length;
    }

}
