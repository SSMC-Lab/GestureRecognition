package com.example.monster.airgesture;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Created by WelkinShadow on 2017/10/27.
 */

public class Conditions {

    //Massage.what
    public static final int MESSAGE_PHASE_MODEL =0x100;

    //SP key
    public static final String CURRENT_USER_ID = "current_user_id";

    //Bundle keys
    public static final String TYPE ="type";

    public static final String[] STOKES={"一", " |"," /"," \\", "⊂","⊃"};

    //my uri
    public static final String URI_INTERACTION_SUBMIT = "myapp://interaction/submit";
    public static final String URI_INTERACTION_SELECT_USER = "myapp://interaction/select-user";

    public static final int gestureHengCode = 1;
    public static final int gestureShuCode = 2;
    public static final int gestureZuoXieCode = 3;
    public static final int gestureYouXieCode = 4;
    public static final int gestureZuoHuCode = 5;
    public static final int gestureYouHuCode = 6;

    private final static String sAbsolutePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/data/files/phase/";  //存放数据的绝对路径
    public static String sFileTemplatePath = sAbsolutePath + "/template/";
    public static String sFileResultPath = sAbsolutePath + "/result/";

    public static File fAbsolutePath = new File(sAbsolutePath);
    public static File fTemplatePath = new File(sFileTemplatePath);
    public static File fResultPath = new File(sFileResultPath);

    public static String getRecordedFileName(String extensionName) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH'h'-mm'm'-ss's'");
        return extensionName + df.format(System.currentTimeMillis());
    }
}
