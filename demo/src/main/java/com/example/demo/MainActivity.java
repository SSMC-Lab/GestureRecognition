package com.example.demo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monster.airgesture.phase.Gesture;
import com.example.monster.airgesture.phase.IPhaseBiz;
import com.example.monster.airgesture.phase.PhaseBizProvider;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 0;

    private TextView textGesture;
    private Button btStart;
    private Button btStop;
    private IPhaseBiz mPhaseBiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //动态申请权限
        checkPermissionAndRequest(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO});

        textGesture = findViewById(R.id.tv_gesture);
        btStart = findViewById(R.id.bt_start);
        btStop = findViewById(R.id.bt_stop);

        //mPhaseBiz = PhaseBizProvider.providePhaseBiz(MainActivity.this,44100,
        //        AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT,44100,24000);
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //1.得到PhaseBiz对象的实例，需要写文件的权限
                mPhaseBiz = PhaseBizProvider.providePhaseBiz(MainActivity.this);
                //2.调用PhaseBiz的方法启业务功能
                mPhaseBiz.startRecognition(new IPhaseBiz.PhaseListener() {
                    @Override
                    public void receiveActionType(Gesture type) {
                        Log.d("MainActivity", type.getStoke());
                        //3.在回调接口得到单个手势对象，更新UI
                        textGesture.append(type.getStoke());
                        //如果需要，验证手势类型并做出其他的操作处理
                        if (type.equals(Gesture.HENG)) {
                            Log.d("MainActivity", "This is heng");
                            //在这里实现具体交互逻辑
                        }
                    }
                });

            }
        });

        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //4.手动关闭功能释放资源
                mPhaseBiz.stopRecognition();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPhaseBiz != null) {
            mPhaseBiz.stopRecognition();
        }
    }

    private void checkPermissionAndRequest(String[] permissions) {
        List<String> permissionToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionToRequest.add(permission);
            }
        }
        if (!permissionToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionToRequest.toArray(new String[permissionToRequest.size()]),
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
