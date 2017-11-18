package com.example.monster.airgesture.data;

import com.example.monster.airgesture.data.bean.Word;
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
    List<Word> getWords(String coding);

    /**
     * 获取关联词集
     * @param word 要查找的单词
     */
    List<Word> getContacted(String word);

    /**
     * 得到从0-9的单词集
     */
    List<Word> getNum();


}
