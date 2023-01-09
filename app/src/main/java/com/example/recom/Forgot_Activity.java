package com.example.recom;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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
                if(binding.emailtxt.getText().toString().isEmpty()){
                    Toast.makeText(Forgot_Activity.this, "Please enter your email.", Toast.LENGTH_LONG).show();
                    binding.emailtxt.setError("Email address is required.");
                    binding.emailtxt.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(binding.emailtxt.getText().toString()).matches()){
                    Toast.makeText(Forgot_Activity.this, "Please re-enter your email address.", Toast.LENGTH_LONG).show();
                    binding.emailtxt.setError("Valid email address is required.");
                    binding.emailtxt.requestFocus();
                }
                else {
                    forgotpass(binding.emailtxt.getText().toString());
                }
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

                    showAlertDialog();
                }
            }
        });

    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Forgot_Activity.this);
        builder.setTitle("Password Reset Confirmation");
        builder.setMessage("We've sent an email to reset your password.Please check your inbox. Thank you!");
        //close
        builder.setPositiveButton("Return to Sign-in", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Forgot_Activity.this, Home.class);
                startActivity(intent);
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

