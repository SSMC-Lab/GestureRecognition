package com.example.monster.airgesture.utils

/**
 * 提供字母和字母在字母表中位置的映射
 * Created by Welkinshadow on 2018/1/21.
 */

object AlphabetUtils {

    val letter = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    /**
     * 返回字母在字母表中的顺序
     */
    fun getPosition(letter: Char): Int {
        when (letter) {
            'a', 'A' -> return 0
            'b', 'B' -> return 1
            'c', 'C' -> return 2
            'd', 'D' -> return 3
            'e', 'E' -> return 4
            'f', 'F' -> return 5
            'g', 'G' -> return 6
            'h', 'H' -> return 7
            'i', 'I' -> return 8
            'j', 'J' -> return 9
            'k', 'K' -> return 10
            'l', 'L' -> return 11
            'm', 'M' -> return 12
            'n', 'N' -> return 13
            'o', 'O' -> return 14
            'p', 'P' -> return 15
            'q', 'Q' -> return 16
            'r', 'R' -> return 17
            's', 'S' -> return 18
            't', 'T' -> return 19
            'u', 'U' -> return 20
            'v', 'V' -> return 21
            'w', 'W' -> return 22
            'x', 'X' -> return 23
            'y', 'Y' -> return 24
            'z', 'Z' -> return 25
            else -> return 0
        }
    }

    /**
     * 返回字母表某位置的字母
     */
    fun getLetter(position:Int):Char{
        return letter[position]
    }
}
