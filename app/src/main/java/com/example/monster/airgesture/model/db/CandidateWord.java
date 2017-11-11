package com.example.monster.airgesture.model.db;

/**
 * 可选词的实体类封装
 * Created by WelkinShadow on 2017/10/27.
 */

public class CandidateWord extends Word {
    private String coding;
    private double probability;
    private int length;

    public CandidateWord(String word, double probability, String coding, int length) {
        this.word = word;
        this.probability = probability;
        this.coding = coding;
        this.length = length;
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
