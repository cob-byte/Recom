package com.example.recom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.recom.databinding.ActivityChangePasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {
    private ActivityChangePasswordBinding binding;
    private final FirebaseAuth firebaseProfile = FirebaseAuth.getInstance();
    private final FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        if(firebaseUser != null){
            binding.btnconfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!binding.typePastPass.getText().toString().isEmpty() && binding.typePastPass.getText().toString().length() > 6){
                        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), binding.typePastPass.getText().toString());

                        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String newpass = binding.TypeNewPass.getText().toString();
                                String cpass = binding.ConfirmNewPass.getText().toString();
                                if(newpass.isEmpty()){
                                    Toast.makeText(ChangePassword.this, "Please enter your password.", Toast.LENGTH_LONG).show();
                                    binding.TypeNewPass.setError("Password is required.");
                                    binding.TypeNewPass.requestFocus();
                                }
                                else if(cpass.isEmpty()){
                                    Toast.makeText(ChangePassword.this, "Please confirm your password.", Toast.LENGTH_LONG).show();
                                    binding.ConfirmNewPass.setError("Password confirmation is required.");
                                    binding.ConfirmNewPass.requestFocus();
                                }
                                else if(newpass.length() < 6){
                                    Toast.makeText(ChangePassword.this, "Password should be at least 6 digits", Toast.LENGTH_LONG).show();
                                    binding.TypeNewPass.setError("Password is too weak");
                                    binding.TypeNewPass.requestFocus();
                                }
                                else if(!newpass.equals(cpass)){
                                    Toast.makeText(ChangePassword.this, "Please re-enter your password.", Toast.LENGTH_LONG).show();
                                    binding.ConfirmNewPass.setError("Password doesn't match.");
                                    binding.ConfirmNewPass.requestFocus();
                                    //clear
                                    binding.TypeNewPass.clearComposingText();
                                    binding.ConfirmNewPass.clearComposingText();
                                }
                                else{
                                    firebaseUser.updatePassword(newpass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(ChangePassword.this, "Password changed, you will automatically be logged out.", Toast.LENGTH_LONG).show();
                                            firebaseProfile.signOut();
                                            Intent logout = new Intent(ChangePassword.this, MainActivity.class);
                                            logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(logout);
                                            finish();
                                        }
                                    });
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(ChangePassword.this, "Something went wrong.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(ChangePassword.this, "Something went wrong.", Toast.LENGTH_LONG).show();
        }

        binding.Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}