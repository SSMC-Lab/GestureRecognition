package com.example.monster.airgesture.model.db;

import android.app.Application;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.test.AndroidTestCase;
import android.test.ApplicationTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import com.example.monster.airgesture.ApplicationTest;
import com.example.monster.airgesture.ui.input.InputActivity;
import com.example.monster.airgesture.ui.test.MainActivity;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by WelkinShadow on 2017/11/14.
 */
public class WordQueryImplTest {

    private static final String TAG = "WordQueryImplTest";
    WordQuery query;
    Context mMockContext;

    @Before
    public void setUp() throws Exception {
        mMockContext = new RenamingDelegatingContext(InstrumentationRegistry.getTargetContext(), "test_");
        query = new WordQueryImpl(mMockContext);
    }

    @Test
    public void getWordList() throws Exception {
        List<Word> words = query.getWordList("1311");
        CandidateWord candidateWord = null;
        String target = "wait";
        String text;
        String coding;
        boolean isContainTarget = false;
        for (int i = 0, len = words.size(); i < len; i++) {
            candidateWord = (CandidateWord) words.get(i);
            text = candidateWord.getWord();
            coding = candidateWord.getCoding();
            if (text == target) {
                isContainTarget = true;
            }
            Log.i(TAG, "word:" + text + " coding:" + coding);
        }
        Assert.assertTrue(isContainTarget);
    }

}