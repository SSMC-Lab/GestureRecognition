package com.example.monster.airgesture.model.db;

/**
 * Created by WelkinShadow on 2017/11/12.
 */

public class ProbCode {
    private String seq;
    private double WrongProb;

    public ProbCode(String seq, double wrongProb) {
        this.seq = seq;
        WrongProb = wrongProb;
    }

    public String getSeq() {
        return seq;
    }

    public double getWrongProb() {
        return WrongProb;
    }
}
