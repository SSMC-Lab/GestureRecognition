package com.example.monster.airgesture.ui;

import com.example.monster.airgesture.model.db.CandidateDB;
import com.example.monster.airgesture.model.db.CandidateWord;
import java.util.List;

/**
 * Created by WelkinShadow on 2017/10/26.
 */

public interface InputContract {

    interface Presenter<V extends View> extends BaseContract.Presenter<V>{

        /** 和 database 模块的交互 */
        void findWord(String coding);

        void onAttachDB(CandidateDB db);

        void onDetachDB();

        /** 和 phase 模块的交互 */
        void initConfig();

        /** ON/OFF Button */
        void startRecording();

        void stopRecording();
    }

    interface View extends BaseContract.View{

        /** input area */
        void delWord();

        void setWord(String word);

        void clearInput();


        /** input strokes */
        void setStroke(int type);

        void delStroke();

        void clearStroke();


        /** candidate word **/
        void setCandidateWord(List<CandidateWord> candidateWord);

        void clearCandidateWord();
    }
}
