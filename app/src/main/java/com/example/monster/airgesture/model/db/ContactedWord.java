package com.example.monster.airgesture.model.db;

/**
 * 联系词的实体类封装
 * Created by WelkinShadow on 2017/11/8.
 */

public class ContactedWord extends Word{
    private long id;
    private long frequency;
    private String contactedWord;

    public ContactedWord(long id, long frequency, String word, String contactedWord) {
        this.id = id;
        this.frequency = frequency;
        this.word = word;
        this.contactedWord = contactedWord;
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
