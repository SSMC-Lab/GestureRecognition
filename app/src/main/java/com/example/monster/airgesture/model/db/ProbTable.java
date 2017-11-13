package com.example.monster.airgesture.model.db;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by apple on 2017/11/13.
 * ProbCode各种方法实现
 */

class ProbTable {
    public static double[][] createProbMatrix(Context context) throws IOException {
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

    public static boolean checkStr(String seq) {
        if (seq.indexOf('1') != -1 || seq.indexOf('6') != -1 || seq.indexOf('2') != -1)
            return true;
        else
            return false;
    }

    public static String replaceIndex(int index, String res, String str) {
        return res.substring(0, index) + str + res.substring(index + 1);
    }

    public static double calculateCorrectProb(String seq, double[][] probMatrix) {
        double prob = 1;
        for (int i = 0; i < seq.length(); i++) {
            String s = String.valueOf(seq.charAt(i));
            int temp = Integer.parseInt(s);
            prob *= probMatrix[temp - 1][temp - 1];
        }
        return prob;
    }

}