package com.example.monster.airgesture.ui.test;

import android.content.Intent;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Handler;

import com.example.monster.airgesture.Conditions;
import com.example.monster.airgesture.R;
import com.example.monster.airgesture.phase.AudioTrackPlay;
import com.example.monster.airgesture.GlobalConfig;
import com.example.monster.airgesture.ui.input.InputActivity;

/**
 *  这个 activity 被用来测试 phase 模块
 */
public class MainActivity extends AppCompatActivity implements Thread.UncaughtExceptionHandler {
    //////////////////UI///////////////////////////////
    public static TextView tv;
    private String sRecordStatus = "Init Record";
    public static TextView tvTime100MilliSecond;
    private static String INIT_100_MILL_SECOND = "00:00:0";
    private String s100MillSecond = INIT_100_MILL_SECOND;
    private TextView tvType;
    private ListView listView;
    private List<String> types = new ArrayList<>();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int iType = (int) msg.getData().getFloat(Conditions.TYPE);
            tvType.setText("type :" + iType);
            SimpleDateFormat fm = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            Date now = new Date();
            types.add(fm.format(now) + ": type :" + iType);
            listView.deferNotifyDataSetChanged();
            listView.setSelection(ListView.FOCUS_DOWN);
        }
    };

    //////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textView2);
        tvType = (TextView) findViewById(R.id.text_type);
        listView = (ListView) findViewById(R.id.type_listview);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, types));
        //在此调用下面方法，才能捕获到线程中的异常
        Thread.setDefaultUncaughtExceptionHandler(this);
        //time init
        tvTime100MilliSecond = (TextView) findViewById(R.id.time_100millisecond);
        tvTime100MilliSecond.setText(s100MillSecond);
        tv.setText(sRecordStatus);

        if (!Conditions.configInit) {
            GlobalConfig.fAbsolutepath.mkdirs();//创建文件夹
            GlobalConfig.fTemplatePath.mkdirs();//创建文件夹
            GlobalConfig.fResultPath.mkdirs();//创建文件夹
            GlobalConfig.stPhaseProxy.init();
            //initIos();
            if (GlobalConfig.bPlayThreadFlag) {
                //ThreadInstantPlay threadInstantPlay = new ThreadInstantPlay();
                //Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                //threadInstantPlay.start();
                GlobalConfig.stWavePlayer.play();
            } else {
                GlobalConfig.isRecording = true;
            }
            Conditions.configInit = true;
        }

        //startRecordAction();

        GlobalConfig.stPhaseProxy.sendHandler(handler);

    }

    //必须实现接口uncaughtException
    @Override
    public void uncaughtException(Thread arg0, Throwable arg1) {
        //在此处理异常， arg1即为捕获到的异常
        Log.i("AAA", "uncaughtException   " + arg1);
    }

    class ThreadInstantPlay extends Thread {
        @Override
        public void run() {
            //Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
            AudioTrackPlay Player = new AudioTrackPlay();
            GlobalConfig.isRecording = true;
            Player.play();

            while (GlobalConfig.isRecording == true) {
            }
            Player.stop();
        }
    }


    public void startRecordAction() {

        GlobalConfig.stWavRecorder.start();
        sRecordStatus = "-------------start Record-----------------";
        tv.setText(sRecordStatus);

        /*try {
            //创建临时文件,注意这里的格式为.pcm
            GlobalConfig.fPcmRecordFile = File.createTempFile(GlobalConfig.sRecordPcmFileName, ".pcm", GlobalConfig.fAbsolutepath);
            GlobalConfig.fPcmRecordFile2 = File.createTempFile(GlobalConfig.sRecordPcmFileName2, ".pcm", GlobalConfig.fAbsolutepath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        GlobalConfig.stPhaseAudioRecord.initRecord();
        sRecordStatus="-------------start Record-----------------";
        tv.setText(sRecordStatus);*/
    }

    //runs after pressing the record button
    public void startRecording(View view) throws IOException {
        startRecordAction();
        GlobalConfig.fPcmRecordFile2 = new File(GlobalConfig.getRecordedFileName(GlobalConfig.sRecordPcmFileName2));
        //UI
        view.setClickable(false);
        Button btn = (Button) findViewById(R.id.button);
        btn.setClickable(true);
        //judgeEddian();
    }


    public void stopRecordingAction() throws IOException {
        Log.i("timer", "audio Stopped");
        sRecordStatus = "!!!!!!!!!!!!!stop Record!!!!!!!!!!!!";
        tv.setText(sRecordStatus);
        //play stop
        //record release
        GlobalConfig.isRecording = false;
        //GlobalConfig.stPhaseAudioRecord.stopRecording();
        GlobalConfig.stPhaseProxy.destroy();
        GlobalConfig.stWaveFileUtil.destroy();
        GlobalConfig.stWavRecorder.stop();
    }

    //runs when the stop button is pressed
    public void stopRecording(View view) throws IOException {
        Log.i("audio", "Stopped");
        stopRecordingAction();
        //UI
        view.setClickable(false);
        Button btn = (Button) findViewById(R.id.button2);
        btn.setClickable(true);
    }

    //any code below this comment can be neglected
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_main) {
            //跳转到InputActivity
            Intent intent = new Intent(this, InputActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
