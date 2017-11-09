package com.example.monster.airgesture.model.db;

/**
 * Created by lenovo on 2017/11/8.
 */

public class ContactedWord {
    private long id;
    private long frequency;
    private String word;
    private String contacrtedWord;

    public ContactedWord(long id, long frequency, String word, String contacrtedWord) {
        this.id = id;
        this.frequency = frequency;
        this.word = word;
        this.contacrtedWord = contacrtedWord;
    }

    public long getId() {
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
