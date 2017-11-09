package com.example.monster.airgesture.model.db;

import java.util.List;

/**
 * Created by WelkinShadow on 2017/10/26.
 */

public interface DictionaryDB {
    /**
     * 从数据库获取编码对应的单词集合
     * @param coding 编码序列
     * @return 对应编码的单词组成的集合
     */
    List<CandidateWord> getWordList(String coding);

    /**
     * 当编码序列中笔画小于2时，返回对应编码的字母
     *
     * @param type 编码类型
     * @return 对应编码的字母集合
     */
    List<CandidateWord> getLetter(String type);

    /**
     * 得到从0-9的数据
     */
    List<CandidateWord> getNum();

    /**
     * 获取关联词
     */
    List<ContactedWord> getContacted(String word);
}
