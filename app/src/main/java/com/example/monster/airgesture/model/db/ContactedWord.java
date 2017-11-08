package com.example.monster.airgesture.model.db;

/**
 * Created by lenovo on 2017/11/8.
 */

public class ContactedWord {
    private int id;
    private long frequency;
    private String word;
    private String contacrtedWord;

    public ContactedWord(int id, long frequency, String word, String contacrtedWord) {
        this.id = id;
        this.frequency = frequency;
        this.word = word;
        this.contacrtedWord = contacrtedWord;
    }

    public int getId() {
        return id;
    }

    public long getFrequency() {
        return frequency;
    }

    public String getWord() {
        return word;
    }

    public String getContacrtedWord() {
        return contacrtedWord;
    }
}
