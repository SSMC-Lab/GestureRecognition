package com.example.monster.airgesture.data.database;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * ProbCode操作的工具类
 * Created by apple on 2017/11/13.
 */

class ProbTableUtil {

    /**
     * 读取assets下的转移矩阵
     */
    static double[][] createProbMatrix(Context context) throws IOException {
        double[][] probMatrix = new double[6][6];
        InputStream inputStream = context.getAssets().open("probMatrix.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        int row = 0;
        while ((line = in.readLine()) != null) {
            String[] temp = line.split("\t");
            for (int j = 0; j < temp.length; j++)
                probMatrix[row][j] = Double.parseDouble(temp[j]);
            row++;
        }
        in.close();
        return probMatrix;
    }

    /**
     * 检查传入的序列是否存在纠错序列，不存在纠错序列返回false
     */
    static boolean checkStr(String seq) {
        return seq.indexOf('1') != -1 || seq.indexOf('6') != -1 || seq.indexOf('2') != -1;
    }

    static String replaceIndex(int index, String res, String str) {
        return res.substring(0, index) + str + res.substring(index + 1);
    }

    /**
     * 计算出纠错序列
     *
     * @param seq        待纠错序列
     * @param probMatrix 转移矩阵
     * @return 纠错序列
     */
    static double calculateCorrectProb(String seq, double[][] probMatrix) {
        double prob = 1;
        for (int i = 0; i < seq.length(); i++) {
            String s = String.valueOf(seq.charAt(i));
            int temp = Integer.parseInt(s);
            prob *= probMatrix[temp - 1][temp - 1];
        }
        return prob;
    }

}