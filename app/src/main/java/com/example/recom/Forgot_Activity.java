package com.example.recom;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.recom.databinding.ActivityForgot2Binding;
import com.example.recom.databinding.ActivityForgot2Binding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forgot_Activity extends AppCompatActivity {
    private ActivityForgot2Binding binding;
    private Animation btnAnim ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgot2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //button back
        binding.btnBackForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(Forgot_Activity.this, SignIn.class);
                startActivity(back);
            }
        });
        binding.btnconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.emailtxt.getText().toString();
                if (!email.isEmpty()) {
                    forgotpass(email);

                }
                else {
                    Toast.makeText(Forgot_Activity.this, "Please enter your email address.", Toast.LENGTH_LONG).show();

                }
            }
        });
        binding.btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resetpass= new Intent(Forgot_Activity.this, SignIn.class);
                startActivity(resetpass);
            }
        });

        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);

    }

    private void forgotpass(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Email Sent.");

                    //to visible confirmation text
                    binding.bgExit.setVisibility(View.VISIBLE);
                    binding.btnclose.setVisibility(View.VISIBLE);
                    binding.confrimText.setVisibility(View.VISIBLE);

                    // TODO : ADD an animation the confirmation text button
                    binding.bgExit.setAnimation(btnAnim);
                    binding.btnclose.setAnimation(btnAnim);
                    binding.confrimText.setAnimation(btnAnim);

                }
            }
        });

    }
}

