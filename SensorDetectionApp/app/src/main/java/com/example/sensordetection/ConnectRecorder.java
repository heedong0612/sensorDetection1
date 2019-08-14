package com.example.sensordetection;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

public class ConnectRecorder extends AppCompatActivity {

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_recorder);
        SensorApplication app = (SensorApplication) getApplication();
        mSocket = app.getSocket();
        mSocket.connect();
        mSocket.emit("join recorder");
        mSocket.on("start record", onRecord);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.off("start record", onRecord);
    }

    private void letsRecord() {
        Intent recorderIntent = new Intent(this, ActivateRecorder.class);
        startActivity(recorderIntent);
    }

    private Emitter.Listener onRecord = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run(){
                    letsRecord();
                }
            });
        }
    };
}
