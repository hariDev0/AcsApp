package com.irya.acsapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import java.util.Timer;
import java.util.TimerTask;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        getSupportActionBar().hide();

    TimerTask ts= new TimerTask() {
        @Override
        public void run() {

            Intent it=new Intent(LaunchActivity.this,LoginActivity.class);
            startActivity(it);
            finish();
        }
    };

        new Timer().schedule(ts,5000);

    }
}
