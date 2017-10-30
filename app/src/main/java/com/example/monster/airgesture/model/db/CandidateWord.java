package com.example.monster.airgesture.model.db;

/**
 * Created by WelkinShadow on 2017/10/27.
 */

public class CandidateWord {
    private String word;
    private double frequency;
    private String coding;
    private int length;

    public CandidateWord(String word, double frequency, String coding, int length) {
        this.word = word;
        this.frequency = frequency;
        this.coding = coding;
        this.length = length;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public String getCoding() {
        return coding;
    }

    public void setCoding(String coding) {
        this.coding = coding;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
