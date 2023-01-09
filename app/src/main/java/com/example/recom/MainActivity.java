package com.example.recom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.recom.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // hide the action bar
        getSupportActionBar().hide();

        binding.signinbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signin = new Intent(getApplicationContext(), SignIn.class);
                startActivity(signin);
            }
        });

        binding.signupbutton.setOnClickListener(new View.OnClickListener() {
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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

            databaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@com.google.firebase.database.annotations.NotNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        // user has sign out
                        Toast.makeText(MainActivity.this, "User has been logged out.", Toast.LENGTH_LONG).show();

                        mAuth.signOut();
                    } else {
                        if (!user.isEmailVerified()) {
                            //resend verification email
                            user.sendEmailVerification();
                        } else {
                            // user still logged in
                            Toast.makeText(MainActivity.this, "User has already logged in.", Toast.LENGTH_LONG).show();

                            //new activity
                            startActivity(new Intent(MainActivity.this, dashboard.class));
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Something went wrong! User details are not available at the moment.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}