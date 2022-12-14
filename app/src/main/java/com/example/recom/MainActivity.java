package com.example.recom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator3;

public class MainActivity extends AppCompatActivity {

    Button signinbutton, signupbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hide the action bar

        getSupportActionBar().hide();

        signinbutton = (Button) findViewById(R.id.signinbutton);
        signupbutton = (Button) findViewById(R.id.signupbutton);


        signinbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signin = new Intent(getApplicationContext(), SignIn.class);
                startActivity(signin);
            }
        });

        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signup = new Intent(getApplicationContext(), Registration.class);
                startActivity(signup);
            }
        });

    }

    //check first if the user is logged in or not
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Toast.makeText(MainActivity.this, "User already logged in.", Toast.LENGTH_LONG).show();

            //new activity
            startActivity(new Intent(MainActivity.this, dashboard.class));
            finish();
        }
    }
}