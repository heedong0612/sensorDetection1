package com.example.sensordetection;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }

    public void choosePlayer(View view){
        Intent playerIntent = new Intent(this, ConnectPlayer.class);
        startActivity(playerIntent);
    }

    public void chooseRecorder(View view){
        Intent recorderIntent = new Intent(this, ConnectRecorder.class);
        startActivity(recorderIntent);
    }

}