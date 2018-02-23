package com.example.monster.airgesture.data;

import com.example.monster.airgesture.data.bean.Word;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by WelkinShadow on 18/2/23.
 */
public class DataRepositoryTest {
    IDataSource dataRepository;

    @Before
    public void setUp() throws Exception {
        dataRepository = DataProvider.provideDataRepository();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void findWords() throws Exception {
        boolean condition = false;
        int count = 0;
        List<Word> result;
        dataRepository.setCurrentUser(dataRepository.getDefaultUser());
        //对单词查询正确率的检查
        result = dataRepository.findWords("411213");
        for (Word word : result) {
            if (word.getWord().equals("within")) {
                condition = true;
                break;
            }
        }
        //对字母查询正确率的检查
        result = dataRepository.findWords("1");
        for (Word word : result) {
            switch (word.getWord()) {
                case "I":
                case "T":
                case "Z":
                case "J":
                    count++;
                default:
                    break;
            }
        }
        condition = condition && count == 4;
        Assert.assertTrue(condition);
    }

    @Test
    public void findContactedWord() throws Exception {
        boolean condition = false;
        List<Word> result;
        result = dataRepository.findContactedWord("consider");
        for (Word word : result) {
            if (word.getWord().equals("their")) {
                condition = true;
                break;
            }
        }
        Assert.assertTrue(condition);
    }

    @Test
    public void getNum() throws Exception {
        boolean condition = true;
        int[] num = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int i = 0;
        for (Word word : dataRepository.getNum()) {
            if (!word.getWord().equals(String.valueOf(num[i]))){
                condition = false;
                break;
            }
            i++;
        }
        Assert.assertTrue(condition);
    }

    @Test
    public void setCurrentUser() throws Exception {

    }

    @Test
    public void resetCurrentUser() throws Exception {
        try{
            dataRepository.resetCurrentUser();
        }catch (Exception e){
            Assert.fail();
        }
        Assert.assertTrue(true);
    }

    @Test
    public void createDictionaryByHabit() throws Exception {
    }

    @Test
    public void findAllUsers() throws Exception {
    }

    @Test
    public void saveUser() throws Exception {
    }

    @Test
    public void deleteUser() throws Exception {
    }

    @Test
    public void updateUser() throws Exception {
    }

    @Test
    public void getDefaultUser() throws Exception {
        try{
            dataRepository.getDefaultUser();
        }catch (Exception e){
            Assert.fail();
        }
        Assert.assertTrue(true);
    }

}