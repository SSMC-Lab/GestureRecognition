package com.example.monster.airgesture.model.db;

import java.util.List;

/**
 * Created by WelkinShadow on 2017/10/26.
 */

public interface CandidateDB {
    /**
     * 从数据库获取编码对应的单词集合
     * @param coding 编码序列
     * @param sortMode  排序模式
     * @return 对应编码的单词组成的集合
     */
    List<String> getWordList(int coding, int sortMode);

    /**
     * 当编码序列中笔画小于2时，返回对应编码的字母
     * @param type 编码类型
     * @return 对应编码的字母集合
     */
    List<String> getLetter(int type);
}
