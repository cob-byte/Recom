package com.example.recom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;

public class Splash extends AppCompatActivity {
    private Handler handler;

    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        videoView = (VideoView)findViewById(R.id.videoView2);
        String videoPath = new StringBuilder("android.resource://")
                .append(getPackageName())
                        .append("/raw/splash")
                                .toString();
        videoView.setVideoPath(videoPath);

        //Event
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                getSupportActionBar().hide();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Splash.this, activityIntro.class);
                        startActivity(intent);
                    }
                }, 1500);
            }
        });
        videoView.setZOrderOnTop(true);
        videoView.start();
    }
}