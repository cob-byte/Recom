package com.example.recom;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.recom.databinding.ActivityForgot2Binding;
import com.example.recom.databinding.ActivityForgot2Binding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forgot_Activity extends AppCompatActivity {
    private ActivityForgot2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgot2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //button back
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
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
    }

    private void forgotpass(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Email Sent.");

                }
                Intent resetpass= new Intent(Forgot_Activity.this, SignIn.class);
                startActivity(resetpass);
            }
        });
    }
}

