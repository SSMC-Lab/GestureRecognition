package com.example.monster.airgesture.model.db;

import java.util.List;

/**
 * 数据库管理接口，负责和数据库的创建和查询
 * Created by WelkinShadow on 2017/10/26.
 */

public interface WordQuery {
    /**
     * 从数据库获取编码对应的单词集
     * @param coding 编码序列
     */
    List<Word> getWordList(String coding);

    /**
     * 当编码序列中笔画小于2时，返回对应编码的字母集
     * @param type 编码类型
     */
    List<Word> getLetter(String type);

    /**
     * 得到从0-9的单词集
     */
    List<Word> getNum();

    /**
     * 获取关联词集
     * @param word 要查找的单词
     */
    List<Word> getContacted(String word);
}
