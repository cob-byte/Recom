package com.example.recom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recom.databinding.ActivityMainBinding;
import com.example.recom.databinding.ActivityRegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {
    private ActivityRegistrationBinding binding;
    private RadioButton selectedGender;
    private DatabaseReference reference;
    private DatePickerDialog picker;
    private static final String TAG = "Registration";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        //Gender
        binding.registerGender.clearCheck();

        binding.dateofbirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //date picker
                picker = new DatePickerDialog(Registration.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        binding.dateofbirth.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedGenderID = binding.registerGender.getCheckedRadioButtonId();
                selectedGender = findViewById(selectedGenderID);
                String Fname = binding.Fname.getText().toString();
                String Mname = binding.Mname.getText().toString();
                String Lname = binding.Lname.getText().toString();
                String address = binding.address.getText().toString();
                String email = binding.emailadd.getText().toString();
                String phone = binding.phonenum.getText().toString();
                String birth = binding.dateofbirth.getText().toString();
                String password = binding.password.getText().toString();
                String cpassword = binding.cpassword.getText().toString();
                String gender;

                //check mobile number
                String mobilenumber = "[0][9][0-9]{9}";
                Matcher matcher;
                Pattern mobilePattern = Pattern.compile(mobilenumber);
                matcher = mobilePattern.matcher(phone);

                if(Fname.isEmpty()){
                    Toast.makeText(Registration.this, "Please enter your first name.", Toast.LENGTH_LONG).show();
                    binding.Fname.setError("First name is required.");
                    binding.Fname.requestFocus();
                }
                else if(Mname.isEmpty()){
                    Toast.makeText(Registration.this, "Please enter your middle name.", Toast.LENGTH_LONG).show();
                    binding.Mname.setError("Middle name is required.");
                    binding.Mname.requestFocus();
                }
                else if(Lname.isEmpty()){
                    Toast.makeText(Registration.this, "Please enter your last name.", Toast.LENGTH_LONG).show();
                    binding.Lname.setError("Last name is required.");
                    binding.Lname.requestFocus();
                }
                else if(address.isEmpty()){
                    Toast.makeText(Registration.this, "Please enter your address.", Toast.LENGTH_LONG).show();
                    binding.Lname.setError("Postal Address is required.");
                    binding.Lname.requestFocus();
                }
                else if(email.isEmpty()){
                    Toast.makeText(Registration.this, "Please enter your email.", Toast.LENGTH_LONG).show();
                    binding.emailadd.setError("Email address is required.");
                    binding.emailadd.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(Registration.this, "Please re-enter your email address.", Toast.LENGTH_LONG).show();
                    binding.emailadd.setError("Valid email address is required.");
                    binding.emailadd.requestFocus();
                }
                else if(phone.isEmpty()){
                    Toast.makeText(Registration.this, "Please enter your phone number.", Toast.LENGTH_LONG).show();
                    binding.phonenum.setError("Phone number is required.");
                    binding.phonenum.requestFocus();
                }
                else if(phone.length() != 11){
                    Toast.makeText(Registration.this, "Please re-enter your phone number.", Toast.LENGTH_LONG).show();
                    binding.phonenum.setError("Phone number must be 11 digits.");
                    binding.phonenum.requestFocus();
                }
                else if(!matcher.find()){
                    Toast.makeText(Registration.this, "Please re-enter your phone number.", Toast.LENGTH_LONG).show();
                    binding.phonenum.setError("Mobile no. is not valid.");
                    binding.phonenum.requestFocus();
                }
                else if(birth.isEmpty()){
                    Toast.makeText(Registration.this, "Please enter your date of birth.", Toast.LENGTH_LONG).show();
                    binding.dateofbirth.setError("Date of birth is required.");
                    binding.dateofbirth.requestFocus();
                }
                else if(binding.registerGender.getCheckedRadioButtonId() == -1){
                    Toast.makeText(Registration.this, "Please select your gender.", Toast.LENGTH_LONG).show();
                    selectedGender.setError("Gender is required.");
                    selectedGender.requestFocus();
                }
                else if(password.isEmpty()){
                    Toast.makeText(Registration.this, "Please enter your password.", Toast.LENGTH_LONG).show();
                    binding.password.setError("Password is required.");
                    binding.password.requestFocus();
                }
                else if(cpassword.isEmpty()){
                    Toast.makeText(Registration.this, "Please confirm your password.", Toast.LENGTH_LONG).show();
                    binding.cpassword.setError("Password confirmation is required.");
                    binding.cpassword.requestFocus();
                }
                else if(password.length() < 6){
                    Toast.makeText(Registration.this, "Password should be at least 6 digits", Toast.LENGTH_LONG).show();
                    binding.password.setError("Password is too weak");
                    binding.password.requestFocus();
                }
                else if(!password.equals(cpassword)){
                    Toast.makeText(Registration.this, "Please re-enter your password.", Toast.LENGTH_LONG).show();
                    binding.cpassword.setError("Password doesn't match.");
                    binding.cpassword.requestFocus();
                    //clear
                    binding.password.clearComposingText();
                    binding.cpassword.clearComposingText();
                }
                else{
                    gender = selectedGender.getText().toString();
                    //new user
                    String newUser = "No";
                    registerUser(Fname, Mname, Lname, address, email, phone, birth, gender, password, newUser);
                }

            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(Registration.this, MainActivity.class);
                startActivity(back);
            }
        });
    }

    //register user
    private void registerUser(String fname, String mname, String lname, String address, String email, String phone, String birth, String gender, String password, String newuser) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();

                    //user display name
                    String Fullname = fname + ' ' + mname + ' ' + lname;
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(Fullname).build();
                    user.updateProfile(profileChangeRequest);

                    //call user java
                    User newUser = new User(fname, mname, lname, address, phone, birth, gender, newuser);


                    //store data in database
                    reference = db.getReference("Users");
                    reference.child(user.getUid()).setValue(newUser).addOnCompleteListener(Registration.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Registration.this, "User registered successfully.", Toast.LENGTH_LONG).show();

                                //send verification email
                                user.sendEmailVerification();

                                //continue
                                Intent login = new Intent(Registration.this, SignIn.class);
                                login.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(login);
                                finish();
                            }
                            else{
                                Toast.makeText(Registration.this, "User registered failed. Please try again.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else{
                    try{
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                        // check if email already exists
                        binding.emailadd.setError("Your email is invalid or already in use. Use another email.");
                        binding.emailadd.requestFocus();
                    }
                    catch (Exception e){
                        //for errors
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(Registration.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}