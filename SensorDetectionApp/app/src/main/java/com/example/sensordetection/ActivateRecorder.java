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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivateRecorder extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecordTest";
//    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    private MediaRecorder recorder = null;
    private Socket mSocket;

    private MediaPlayer   player = null;

//    //Requesting permission to RECORD_AUDIO
//    private boolean permissionToRecordAccepted = false;
//    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode){
//            case REQUEST_RECORD_AUDIO_PERMISSION:
//                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                break;
//        }
//        if (!permissionToRecordAccepted ) finish();
//
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_recorder);

//        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        SensorApplication app = (SensorApplication) getApplication();
        mSocket = app.getSocket();

        fileName = getExternalCacheDir().getAbsolutePath();
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss'.3gp'").format(new Date());
        fileName += "/audiorecordtest_";
        fileName += timestamp;

        startRecording();
        //String deviceName = android.os.Build.MODEL;     // added 08/12
        //mSocket.emit("join recorder"); //args will be device name, research how to get device name from android
        //mSocket.on("start record", onStart);

    }

    private Emitter.Listener onRecStop = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run(){
                    stopRecording();
                }
            });

        }
    };

//    private Emitter.Listener onStart = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run(){
//                    mSocket.off( "start record", onStart);
//                    startRecording();
//                    mSocket.on("stop record", onRecStop);
//                }
//            });
//        }
//    };

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

    private void startRecording() {
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

    private byte[] getBytes(File f)
            throws IOException
    {
        byte[] buffer = new byte [1024];
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        FileInputStream fis = new FileInputStream(f);
        int read;
        while((read = fis.read(buffer)) != -1)
        {
            os.write(buffer, 0, read);
        }
        fis.close();
        os.close();
        return os.toByteArray();
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

        //convert file to bytearray
        try {
            File fileToSend = new File(fileName);
            byte[] byteArr = getBytes(fileToSend);
            mSocket.emit("Send File", byteArr);
        }
        catch (Exception e){
            Log.e(LOG_TAG, "No File Found");
        }

        Intent recorderIntent = new Intent(this, FinishRecording.class);
        startActivity(recorderIntent);
    }
}
