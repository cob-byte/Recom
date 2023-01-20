package com.example.recom;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class LoadScreen extends AppCompatActivity {
        private Handler handler;

        VideoView vvload;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.loading_screen);

            vvload = (VideoView)findViewById(R.id.vv_load);
            String videoPath = new StringBuilder("android.resource://")
                    .append(getPackageName())
                    .append("/raw/splash")
                    .toString();
            vvload.setVideoPath(videoPath);

            //Event
           // vvload.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
             //   @Override
              //  public void onCompletion(MediaPlayer mediaPlayer) {
                //    getSupportActionBar().hide();
                  //  Intent intent = new Intent(LoadScreen.this, Home.class);
                   // startActivity(intent);
               // }
            //});
            vvload.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    //create a new instance of the Home fragment
                    Home homeFragment = new Home();
                    //begin a fragment transaction
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //replace the current fragment with the Home fragment
                    fragmentTransaction.replace(R.id.frameLayout, homeFragment);
                    fragmentTransaction.commit();
                    finish();
                }
            });
            vvload.setZOrderOnTop(true);
            vvload.start();
        }
    }
