package com.example.sensordetection;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

public class ConnectPlayer extends AppCompatActivity {

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_player);

        SensorApplication app = (SensorApplication) getApplication();
        mSocket = app.getSocket();
        mSocket.emit("join player");

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
}
