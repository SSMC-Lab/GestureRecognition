package com.example.monster.airgesture.model.db.module;

/**
 * 纠错序列，用于纠正手势识别错误问题
 * Created by WelkinShadow on 2017/11/12.
 */

public class ProbCode {
    private String seq;
    private double WrongProb;

    /**
     * 数据表字段名
     */
    public static final String TABLE_NAME = "seq";
    public static final String STROKES = "strokes";
    public static final String BAYESPROB ="bayesProb";

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
