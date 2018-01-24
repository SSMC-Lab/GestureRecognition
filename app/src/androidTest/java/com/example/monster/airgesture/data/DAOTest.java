package com.example.monster.airgesture.data;

import com.example.monster.airgesture.data.bean.CandidateWord;
import com.example.monster.airgesture.data.bean.User;
import com.example.monster.airgesture.data.bean.Word;
import com.example.monster.airgesture.utils.AlphabetUtils;
import com.example.monster.airgesture.utils.LogUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by Welkinshadow on 2018/1/20.
 */
public class DAOTest {
    private IWordDAO wordDAO;
    private IUserDAO userDAO;

    @Before
    public void setUp() throws Exception {
        wordDAO = DataFactory.getWordDAO();
        userDAO = DataFactory.getUserDAO();
    }

    /** 基于单词默认编码的测试 */
    @Test
    public void getWordList(){
        Assert.assertTrue(isContainTarget("test","1251",userDAO.getDefaultUser()));
    }

    @Test
    public void createDictionaryByHabit() throws Exception {
        //标准编码
        List<Integer> code = userDAO.getDefaultUser().getLetterMapping();
        code.set(AlphabetUtils.INSTANCE.getPosition('F'),1);//E=1
        final User user = new User("Fu Hong",code);
        userDAO.createDictionaryByHabit(user, new IUserDAO.OnCreateListener() {
            @Override
            public void OnStart() {
                LogUtils.i("Start");
            }

            @Override
            public void OnSuccessful() {
                LogUtils.i("Successful");
                boolean result = isContainTarget("find","1136",user);
                Assert.assertTrue(result);
            }

            @Override
            public void OnFailed() {
                LogUtils.i("Failed");
                Assert.assertTrue(false);
            }
        });
    }

    private boolean isContainTarget(String target,String coding,User user){
        wordDAO.attachUser(user);
        List<Word> words = wordDAO.getWords(coding);
        CandidateWord candidateWord;
        String text;
        boolean isContainTarget = false;
        for (int i = 0, len = words.size(); i < len; i++) {
            candidateWord = (CandidateWord) words.get(i);
            text = candidateWord.getWord();
            if (text.equals(target) ) {
                isContainTarget = true;
            }
        }
        LogUtils.d("isContainTarget?"+isContainTarget);
        return isContainTarget;
    }

}