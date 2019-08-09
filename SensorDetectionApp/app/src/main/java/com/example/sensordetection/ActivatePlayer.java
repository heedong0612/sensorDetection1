package com.example.sensordetection;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.github.nkzawa.socketio.client.Socket;

import androidx.appcompat.app.AppCompatActivity;

public class ActivatePlayer extends AppCompatActivity {

    private Socket mSocket;

//    Context context = this;
    MediaPlayer player;

//    Button player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_player);

        SensorApplication app = (SensorApplication) getApplication();
        mSocket = app.getSocket();
        mSocket.emit("join_player");

//        mp = MediaPlayer.create(context, R.raw.song);
//        play = findViewById(R.id.button_player);
//        play.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v) {
//                try {
//                    if(mp.isPlaying()) {
//                        mp.stop();
//                        mp.release();
//                        mp = MediaPlayer.create(context, R.raw.song);
//                        mp.start();
//                    }else{
//                        mp.start();
//                    }
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//
//        });
//        mp.release();
    }

    public void play(View v) {
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.song);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            });
        }

        player.start();
    }

    public void pause(View v) {
        if (player != null) {
            player.pause();
        }
    }

    public void stop(View v) {
        stopPlayer();
    }

    private void stopPlayer() {
        if (player != null) {
            player.release();
            player = null;
            Toast.makeText(this, "MediaPlayer released", Toast.LENGTH_SHORT).show();
        }
        mSocket.emit("stop collection");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        stopPlayer();
    }
}
