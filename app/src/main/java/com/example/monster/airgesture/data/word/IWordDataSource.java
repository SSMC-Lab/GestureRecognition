package com.example.monster.airgesture.data.word;

import com.example.monster.airgesture.data.bean.User;
import com.example.monster.airgesture.data.bean.Word;
import java.util.List;

/**
 * Word 数据库访问控制接口
 * Created by WelkinShadow on 2017/10/26.
 */

public interface IWordDataSource {

    List<Word> getWords(String coding);

    List<Word> getContacted(String word);

    List<Word> getNum();

    void attachUser(User user);

    void detachUser();

}
