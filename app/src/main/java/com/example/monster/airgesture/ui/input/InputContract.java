package com.example.monster.airgesture.ui.input;

import com.example.monster.airgesture.data.WordQuery;
import com.example.monster.airgesture.data.bean.Word;
import com.example.monster.airgesture.ui.base.BaseContract;

import java.util.List;

/**
 * MVP架构接口部分
 * 该接口规定了主界面/输入界面的执行接口
 * Created by WelkinShadow on 2017/10/26.
 */

public interface InputContract {

    interface Presenter<V extends View> extends BaseContract.Presenter<V>{

        /** 和 database 模块的交互 */
        void findWord(String coding);

        void findContacted(String word);

        void changeNumKeyboard();

        void attachQueryModel(WordQuery wordQuery);

        void detachQueryModel();

        /** 和 phase 模块的交互 */
        void initConfig();

        /** ON/OFF Button */
        void startRecording();

        void stopRecording();

        void clearStoker();

        void delStoker();
    }

    interface View extends BaseContract.View{

        /** input area */
        void enterWord(String word);

        /** input strokes */
        void setStroke(int type);

        /** candidate word **/
        void setCandidateWord(List<Word> candidateWord);

        void clearCandidateWord();
    }
}
