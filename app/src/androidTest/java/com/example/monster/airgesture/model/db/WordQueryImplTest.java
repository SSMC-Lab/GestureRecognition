package com.example.monster.airgesture.model.db;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.test.RenamingDelegatingContext;

import com.example.monster.airgesture.model.db.module.CandidateWord;
import com.example.monster.airgesture.model.db.module.Word;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * WordQuery的单元测试
 * Created by WelkinShadow on 2017/11/14.
 */
public class WordQueryImplTest {

    private static final String TAG = "WordQueryImplTest";
    private WordQuery query;
    private Context mMockContext;

    @Before
    public void setUp() throws Exception {
        mMockContext = new RenamingDelegatingContext(InstrumentationRegistry.getTargetContext(), "test_");
        query = new WordQueryImpl(mMockContext);
    }

    @Test
    public void getWordList() throws Exception {
        List<Word> words = query.getWords("1311");
        CandidateWord candidateWord;
        String target = "wait";
        String text;
        boolean isContainTarget = false;
        for (int i = 0, len = words.size(); i < len; i++) {
            candidateWord = (CandidateWord) words.get(i);
            text = candidateWord.getWord();
            if (text.equals(target) ) {
                isContainTarget = true;
            }
        }
        Assert.assertTrue(isContainTarget);
    }

}