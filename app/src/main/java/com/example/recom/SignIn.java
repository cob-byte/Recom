package com.example.recom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import org.w3c.dom.Text;

public class SignIn extends AppCompatActivity {
    private TextView email, password;
    private Button signin;
    private FirebaseAuth mAuth;
    private static final String TAG = "Registration";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        email = findViewById(R.id.email);
        password = findViewById(R.id.txtpassword);
        signin = findViewById(R.id.btnsignin);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = email.getText().toString();
                String Password = password.getText().toString();

                if (Email.isEmpty()){
                    Toast.makeText(SignIn.this, "Please enter your email address.", Toast.LENGTH_LONG).show();
                    email.setError("Email address is required.");
                    email.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
                    Toast.makeText(SignIn.this, "Please re-enter your email address.", Toast.LENGTH_LONG).show();
                    email.setError("Valid email address is required.");
                    email.requestFocus();
                }
                else if(Password.isEmpty()){
                    Toast.makeText(SignIn.this, "Please enter your password.", Toast.LENGTH_LONG).show();
                    email.setError("Password is required.");
                    email.requestFocus();
                }
                else if(Password.length() < 6){
                    Toast.makeText(SignIn.this, "Please re-enter your password.", Toast.LENGTH_LONG).show();
                    email.setError("Valid password is required.");
                    email.requestFocus();
                }
                else {
                    signIn(Email, Password);
                }
            }
        });
    }

    private void signIn(String Email, String password) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(Email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignIn.this, "User login successfully.", Toast.LENGTH_LONG).show();

                    //new activity
                    Intent login = new Intent(SignIn.this, dashboard.class);
                    //clear
                    login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(login);
                    finish();
                }
                else{
                    //error handling/message
                    try{
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                        email.setError("Invalid credentials. Please check and re-enter.");
                        email.requestFocus();
                    }
                    catch (FirebaseAuthInvalidUserException e){
                        email.setError("User does not exists or is no longer valid. Please register again.");
                        email.requestFocus();
                    }
                    catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(SignIn.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}