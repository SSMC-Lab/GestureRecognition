package com.example.monster.airgesture.ui;

import com.example.monster.airgesture.model.db.DatabaseQueryManager;
import com.example.monster.airgesture.model.db.CandidateWord;

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

        void changeNumKeyboard();

        void onAttachDB(DatabaseQueryManager db);

        void onDetachDB();

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

        /** 访问控制 */
        boolean isNumKeyboard();
    }
}
