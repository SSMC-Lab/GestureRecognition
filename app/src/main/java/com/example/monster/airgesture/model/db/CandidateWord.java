package com.example.monster.airgesture.model.db;

/**
 * 可选词的实体类封装
 * Created by WelkinShadow on 2017/10/27.
 */

public class CandidateWord {
    private String word;
    private float probability;
    private String coding;
    private int length;

    public CandidateWord(String word, float probability, String coding, int length) {
        this.word = word;
        this.probability = probability;
        this.coding = coding;
        this.length = length;
    }

    public String getWord() {
        return word;
    }

    public float getProbability() {
        return probability;
    }

    public String getCoding() {
        return coding;
    }

    public int getLength() {
        return length;
    }

}
