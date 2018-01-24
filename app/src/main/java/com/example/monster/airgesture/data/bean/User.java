package com.example.monster.airgesture.data.bean;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * 合法注册用户
 * Created by WelkinShadow on 2018/1/17.
 */

public class User extends DataSupport{
    private int id;
    private String name;
    private String dictionaryName;
    /**
     * letterMapping表示映射关系
     * 表的 position (下标)表示字母在字母中的位置
     * 表的 value (值)表示针对手势的编码
     */
    private List<Integer> letterMapping;

    public User() {
    }

    public User(String name, List<Integer> letterMapping){
        this.name = name;
        this.letterMapping = letterMapping;
        this.dictionaryName = name.replaceAll("\\s","");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Integer> getLetterMapping() {
        return letterMapping;
    }

    public void setLetterMapping(List<Integer> letterMapping) {
        this.letterMapping = letterMapping;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDictionaryName() {
        return dictionaryName;
    }

    public void setDictionaryName(String dictionaryName) {
        this.dictionaryName = dictionaryName;
    }
}
