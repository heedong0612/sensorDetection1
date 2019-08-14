package com.example.sensordetection;

import android.Manifest;
//import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.LinearLayout;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivateRecorder extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    private static String filePath = null;
    private MediaRecorder recorder = null;
    private Socket mSocket;

    private MediaPlayer   player = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_recorder);

        // Record to the external cache directory for visibility
        //fileName = Environment.getDataDirectory().getAbsolutePath() + "/recording123.aac";

        fileName = getExternalCacheDir().getAbsolutePath();
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss'.3gp'").format(new Date());

        fileName += "/audiorecordtest_";
        fileName += timestamp;
//        fileName += ".3gp";

        //filePath = Environment.getDataDirectory().getAbsolutePath();
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        SensorApplication app = (SensorApplication) getApplication();
        mSocket = app.getSocket();
        mSocket.connect();

        String deviceName = android.os.Build.MODEL;     // added 08/12
        mSocket.emit("join recorder"); //args will be device name, research how to get device name from android

        mSocket.on("start record", onStart);


    }

    private Emitter.Listener onRecStop = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
//            Toast.makeText(getApplicationContext(), "HELLO Jer", Toast.LENGTH_LONG).show();
            runOnUiThread(new Runnable() {
                @Override
                public void run(){
                    //mSocket.emit("hey waddup");
                    stopRecording();
                }
            });

        }
    };

    private Emitter.Listener onStart = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
//            Toast.makeText(getApplicationContext(), "HELLO Jer", Toast.LENGTH_LONG).show();
            runOnUiThread(new Runnable() {
                @Override
                public void run(){
                    //mSocket.emit("hey waddup");
                    startRecording();
                }
            });
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }

//    public void startPlaying(View v) {
//        player = new MediaPlayer();
//
//        try {
//            player.setDataSource(fileName);
//            player.prepare();
//            player.start();
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "player prepare() failed");
//        }
//    }

//    public void stopPlaying(View v) {
//        player.stop();
//        player.release();
//        player = null;
//    }

    private void startRecording() {
        mSocket.off( "start record", onStart);
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "recorder prepare() failed");
        }

        recorder.start();
        mSocket.on("stop record", onRecStop);

    }

    private void stopRecording() {


        if (recorder != null) {
            try {
                recorder.stop();
            } catch (RuntimeException stopException) {
//                recording_file.delete();
                recorder.reset();
                return;
            }

            recorder.release();
            recorder = null;
        }




//        recorder.stop();
//        recorder.release();
//        recorder = null;

        Intent recorderIntent = new Intent(this, FinishRecording.class);
        startActivity(recorderIntent);
    }
}
