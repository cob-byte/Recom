package com.example.recom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator3;

public class MainActivity extends AppCompatActivity {

    Button signinbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hide the action bar

        getSupportActionBar().hide();

        signinbutton = (Button) findViewById(R.id.signinbutton);

        signinbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signin = new Intent(getApplicationContext(), SignIn.class);
                startActivity(signin);
            }
        });

    }
}