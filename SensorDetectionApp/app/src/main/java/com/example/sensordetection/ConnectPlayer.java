package com.example.sensordetection;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

public class ConnectPlayer extends AppCompatActivity {

    private Socket mSocket;
    private int numRecorder = 0; //keep track of the number of recorders

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //make it always portrait
        setContentView(R.layout.activity_connect_player);

        SensorApplication app = (SensorApplication) getApplication();
        mSocket = app.getSocket();

        String deviceName = android.os.Build.MODEL;
        mSocket.emit("join player", deviceName);


    }

    public void startProcess(View view){
        mSocket.emit("start collection");
        mSocket.on("start play", onPlay);
//        letsPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.off("start play", onPlay);
    }

    private void letsPlay() {
        Intent playerIntent = new Intent(this, ActivatePlayer.class);
        startActivity(playerIntent);
    }

    private Emitter.Listener onPlay = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            letsPlay();
        }
    };

    //added for update08/14
    //update num recorder
    private void updateNumRecorder()
    {
        TextView numRecorderView = (TextView) findViewById(R.id.numRecorderView);
        numRecorderView.setText("Number of connected recorders: " + numRecorder);
    }

    public void updateNumber()
    {
        mSocket.on("add recorder", updateNumListener); //must update server to work with this
    }

    private Emitter.Listener updateNumListener = new Emitter.Listener() {
            @Override
            public void call(final Object... args){
                updateNumRecorder();
            }
    };


}
