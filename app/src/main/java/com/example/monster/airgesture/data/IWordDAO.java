package com.example.monster.airgesture.data;

import com.example.monster.airgesture.data.bean.User;
import com.example.monster.airgesture.data.bean.Word;
import java.util.List;

/**
 * Word 数据库访问控制接口
 * Created by WelkinShadow on 2017/10/26.
 */

public interface IWordDAO {
    /**
     * 从dictionary获取编码对应的单词集
     * 如果编码序列的长度>=1，返回包含以该序列开头的所有单词集，
     * 如果编码序列的长度=1，返回该序列对应的字母，
     * 其他情况返回一个空集
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

    /**
     * 绑定用户来查找相应用户的dictionary
     */
    void attachUser(User user);

    void detachUser();

}
