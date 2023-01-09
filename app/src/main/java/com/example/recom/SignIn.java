package com.example.recom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recom.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class SignIn extends AppCompatActivity {
    private ActivitySignInBinding binding;
    private FirebaseAuth mAuth;
    private static final String TAG = "Registration";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        //show/hide password
        binding.pwdShowHide.setImageResource(R.drawable.hide_pwd);
        binding.pwdShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.txtpassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //then hide it
                    binding.txtpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //change icon
                    binding.pwdShowHide.setImageResource(R.drawable.hide_pwd);
                }
                else{
                    binding.txtpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    binding.pwdShowHide.setImageResource(R.drawable.show_pwd);
                }
            }
        });
        //forgot password
        binding.forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent for_pass = new Intent(SignIn.this, Forgot_Activity.class);
                startActivity(for_pass);
            }
        });
        //button back
        binding.btnBackSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(SignIn.this, MainActivity.class);
                startActivity(back);
            }
        });

        binding.btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = binding.txtemail.getText().toString();
                String Password = binding.txtpassword.getText().toString();

                if (Email.isEmpty()){
                    Toast.makeText(SignIn.this, "Please enter your email address.", Toast.LENGTH_LONG).show();
                    binding.txtemail.setError("Email address is required.");
                    binding.txtemail.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
                    Toast.makeText(SignIn.this, "Please re-enter your email address.", Toast.LENGTH_LONG).show();
                    binding.txtemail.setError("Valid email address is required.");
                    binding.txtemail.requestFocus();
                }
                else if(Password.isEmpty()){
                    Toast.makeText(SignIn.this, "Please enter your password.", Toast.LENGTH_LONG).show();
                    binding.txtemail.setError("Password is required.");
                    binding.txtemail.requestFocus();
                }
                else if(Password.length() < 6){
                    Toast.makeText(SignIn.this, "Please re-enter your password.", Toast.LENGTH_LONG).show();
                    binding.txtemail.setError("Valid password is required.");
                    binding.txtemail.requestFocus();
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
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(SignIn.this, "User login successfully.", Toast.LENGTH_LONG).show();

                        //new activity
                        Intent login = new Intent(SignIn.this, dashboard.class);
                        //clear
                        login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(login);
                        finish();
                    }
                    else{
                        firebaseUser.sendEmailVerification();
                        mAuth.signOut();

                        showAlertDialog();
                    }
                }
                else{
                    //error handling/message
                    try{
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                        binding.txtemail.setError("Invalid credentials. Please check and re-enter.");
                        binding.txtemail.requestFocus();
                    }
                    catch (FirebaseAuthInvalidUserException e){
                        binding.txtemail.setError("User does not exists or is no longer valid. Please register again.");
                        binding.txtemail.requestFocus();
                    }
                    catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(SignIn.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email before logging in. If you do not see the email in a few minutes, check your “junk mail” folder or “spam” folder.");
        //open email app if the user clicks the button
        builder.setPositiveButton("Verify Now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //open new window
                startActivity(intent);
            }
        });

        builder.setNegativeButton("Close", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}