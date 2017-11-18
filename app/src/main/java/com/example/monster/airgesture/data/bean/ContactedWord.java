package com.example.monster.airgesture.data.bean;

import android.database.Cursor;

/**
 * 联想词的实体类封装
 * Created by WelkinShadow on 2017/11/8.
 */

public class ContactedWord extends Word {
    private long id;
    private long frequency;
    private String contactedWord;

    /**
     * 数据表字段名
     */
    public static final String TABLE_NAME = "gramtable";
    public static final String WORD_ID = "id";
    public static final String FREQUENCY = "frequency";
    public static final String CONTACTED = "word";
    public static final String WORD = "2gram";

    public ContactedWord(long id, long frequency, String word, String contactedWord) {
        this.id = id;
        this.frequency = frequency;
        this.word = word;
        this.contactedWord = contactedWord;
    }

    public ContactedWord(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(WORD_ID));
        frequency = cursor.getLong(cursor.getColumnIndex(FREQUENCY));
        contactedWord = cursor.getString(cursor.getColumnIndex(CONTACTED));
        word = cursor.getString(cursor.getColumnIndex(WORD));
    }

    public long getId() {
        return id;
    }

    public long getFrequency() {
        return frequency;
    }

    public String getContactedWord() {
        return contactedWord;
    }
}
