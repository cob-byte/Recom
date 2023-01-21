package com.example.recom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.recom.databinding.ActivityChangeInfoBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class changeInfo extends AppCompatActivity {
    private ActivityChangeInfoBinding binding;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private final FirebaseAuth firebaseProfile = FirebaseAuth.getInstance();
    private final FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();
    private DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();
        
        //birthdate
        binding.editBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //date picker
                picker = new DatePickerDialog(changeInfo.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        binding.editBirthdate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        //get the type of data will be edited
        Intent intent = getIntent();
        String type = intent.getStringExtra("type").toString();

        //get past phone, birthdate, address
        if(firebaseUser != null){
            String UID = firebaseUser.getUid();
            databaseReference = database.getReference("Users");
            databaseReference.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if(user != null){
                        final String phone = user.phonenum;
                        final String birthdate = user.dobirth;
                        final String address = user.address;
                        setPastInfo(phone, birthdate, address, type);
                        setEditProfileInfo(phone, birthdate, address, type);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        //show/hide based on the type passed
        if(type.equals("fullname")){
            binding.EmailAddress.setVisibility(View.GONE);
            binding.editEmailAddress.setVisibility(View.GONE);
            binding.PhoneNumber.setVisibility(View.GONE);
            binding.editPhoneNumber.setVisibility(View.GONE);
            binding.Birthdate.setVisibility(View.GONE);
            binding.editBirthdate.setVisibility(View.GONE);
            binding.Address.setVisibility(View.GONE);
            binding.editAddress.setVisibility(View.GONE);
        }
        else if(type.equals("email")){
            binding.FirstName.setVisibility(View.GONE);
            binding.editFirstName.setVisibility(View.GONE);
            binding.MiddleName.setVisibility(View.GONE);
            binding.editMiddleName.setVisibility(View.GONE);
            binding.LastName.setVisibility(View.GONE);
            binding.editLastName.setVisibility(View.GONE);
            binding.PhoneNumber.setVisibility(View.GONE);
            binding.editPhoneNumber.setVisibility(View.GONE);
            binding.Birthdate.setVisibility(View.GONE);
            binding.editBirthdate.setVisibility(View.GONE);
            binding.Address.setVisibility(View.GONE);
            binding.editAddress.setVisibility(View.GONE);
        }
        else if(type.equals("phone")){
            binding.FirstName.setVisibility(View.GONE);
            binding.editFirstName.setVisibility(View.GONE);
            binding.MiddleName.setVisibility(View.GONE);
            binding.editMiddleName.setVisibility(View.GONE);
            binding.LastName.setVisibility(View.GONE);
            binding.editLastName.setVisibility(View.GONE);
            binding.EmailAddress.setVisibility(View.GONE);
            binding.editEmailAddress.setVisibility(View.GONE);
            binding.Birthdate.setVisibility(View.GONE);
            binding.editBirthdate.setVisibility(View.GONE);
            binding.Address.setVisibility(View.GONE);
            binding.editAddress.setVisibility(View.GONE);
        }
        else if(type.equals("birthdate")){
            binding.FirstName.setVisibility(View.GONE);
            binding.editFirstName.setVisibility(View.GONE);
            binding.MiddleName.setVisibility(View.GONE);
            binding.editMiddleName.setVisibility(View.GONE);
            binding.LastName.setVisibility(View.GONE);
            binding.editLastName.setVisibility(View.GONE);
            binding.EmailAddress.setVisibility(View.GONE);
            binding.editEmailAddress.setVisibility(View.GONE);
            binding.PhoneNumber.setVisibility(View.GONE);
            binding.editPhoneNumber.setVisibility(View.GONE);
            binding.Address.setVisibility(View.GONE);
            binding.editAddress.setVisibility(View.GONE);
        }
        else if(type.equals("address")){
            binding.FirstName.setVisibility(View.GONE);
            binding.editFirstName.setVisibility(View.GONE);
            binding.MiddleName.setVisibility(View.GONE);
            binding.editMiddleName.setVisibility(View.GONE);
            binding.LastName.setVisibility(View.GONE);
            binding.editLastName.setVisibility(View.GONE);
            binding.EmailAddress.setVisibility(View.GONE);
            binding.editEmailAddress.setVisibility(View.GONE);
            binding.PhoneNumber.setVisibility(View.GONE);
            binding.editPhoneNumber.setVisibility(View.GONE);
            binding.Birthdate.setVisibility(View.GONE);
            binding.editBirthdate.setVisibility(View.GONE);
        }
        else{
            Toast.makeText(changeInfo.this, "Something went wrong! Please try again later.", Toast.LENGTH_LONG).show();
            finish();
        }

        //save
        binding.btnCInfoSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firebaseUser != null){
                    changeInfoCommence(type);
                }
                else{
                    Toast.makeText(changeInfo.this, "Something went wrong! Please try again later.", Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.btnCInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setPastInfo(String phone, String birthdate, String address, String type) {
        if(type.equals("fullname")){
            binding.pastInfo.setText("Past Full Name");
            binding.editPastInfo.setText(firebaseUser.getDisplayName());
        }
        else if(type.equals("email")){
            binding.pastInfo.setText("Past Email");
            binding.editPastInfo.setText(firebaseUser.getEmail());
        }
        else if(type.equals("phone")){
            binding.pastInfo.setText("Past Phone Number");
            binding.editPastInfo.setText(phone);
        }
        else if(type.equals("birthdate")){
            binding.pastInfo.setText("Past Birthdate");
            binding.editPastInfo.setText(birthdate);
        }
        else if(type.equals("address")){
            binding.pastInfo.setText("Past Address");
            binding.editPastInfo.setText(address);
        }
        else{
            Toast.makeText(changeInfo.this, "Something went wrong! Past info cannot be fetched.", Toast.LENGTH_LONG).show();
        }
    }
    private void setEditProfileInfo(String phone, String birthdate, String address, String type){
        if(type.equals("fullname")){
            binding.EditProfileInfo.setText("Edit Full Name");
        }
        else if(type.equals("email")){
            binding.EditProfileInfo.setText("Edit E-mail");
        }
        else if(type.equals("phone")){
            binding.EditProfileInfo.setText("Edit Phone Number");
        }
        else if(type.equals("birthdate")){
            binding.EditProfileInfo.setText("Edit Birthdate");
        }
        else if(type.equals("address")){
            binding.EditProfileInfo.setText("Edit Address");
        }
        else{
            binding.EditProfileInfo.setText("Edit Profile Information");
        }
    }

    private void changeInfoCommence(String type) {
        String UID = firebaseUser.getUid();
        databaseReference = database.getReference("Users");
        databaseReference.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                switch (type) {
                    case "fullname":
                        if(binding.editFirstName.getText().toString().isEmpty()){
                            Toast.makeText(changeInfo.this, "Please enter your first name.", Toast.LENGTH_LONG).show();
                            binding.editFirstName.setError("First name is required.");
                            binding.editFirstName.requestFocus();
                        }
                        else if(binding.editMiddleName.getText().toString().isEmpty()){
                            Toast.makeText(changeInfo.this, "Please enter your middle name.", Toast.LENGTH_LONG).show();
                            binding.editMiddleName.setError("Middle name is required.");
                            binding.editMiddleName.requestFocus();
                        }
                        else if(binding.editLastName.getText().toString().isEmpty()){
                            Toast.makeText(changeInfo.this, "Please enter your last name.", Toast.LENGTH_LONG).show();
                            binding.editLastName.setError("Last name is required.");
                            binding.editLastName.requestFocus();
                        }
                        else{
                            //user display name
                            String Fullname = binding.editFirstName.getText().toString() + ' ' + binding.editMiddleName.getText().toString() + ' ' + binding.editLastName.getText().toString();
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(Fullname).build();
                            firebaseUser.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //create hashmap for the data
                                    HashMap<String, Object> userMap = new HashMap();
                                    userMap.put("fname", binding.editFirstName.getText().toString());
                                    userMap.put("mname", binding.editMiddleName.getText().toString());
                                    userMap.put("lname", binding.editLastName.getText().toString());
                                    //reset of verification since new fullname need niya ulit dumaan sa process ng verification
                                    userMap.put("verification", "No");

                                    //update database
                                    databaseReference.child(firebaseUser.getUid()).updateChildren(userMap);

                                    Toast.makeText(changeInfo.this, "Fullname successfully updated!.", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(changeInfo.this, "Something went wrong! Please try again later.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        break;
                    case "email":
                        if(binding.editEmailAddress.getText().toString().isEmpty()){
                            Toast.makeText(changeInfo.this, "Please enter your email.", Toast.LENGTH_LONG).show();
                            binding.editEmailAddress.setError("Email address is required.");
                            binding.editEmailAddress.requestFocus();
                        }
                        else if(!Patterns.EMAIL_ADDRESS.matcher(binding.editEmailAddress.getText().toString()).matches()){
                            Toast.makeText(changeInfo.this, "Please re-enter your email address.", Toast.LENGTH_LONG).show();
                            binding.editEmailAddress.setError("Valid email address is required.");
                            binding.editEmailAddress.requestFocus();
                        }
                        else {
                            //update firebase
                            firebaseUser.updateEmail(binding.editEmailAddress.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(changeInfo.this, "Email successfully updated! Please check your email's inbox for activation of account.", Toast.LENGTH_LONG).show();

                                                finish();
                                            }
                                        });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(changeInfo.this, "Something went wrong! Please try again later.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        break;
                    case "phone":
                        //check mobile number
                        String mobilenumber = "[0][9][0-9]{9}";
                        Matcher matcher;
                        Pattern mobilePattern = Pattern.compile(mobilenumber);
                        matcher = mobilePattern.matcher(binding.editPhoneNumber.getText().toString());

                        if(binding.editPhoneNumber.getText().toString().isEmpty()){
                            Toast.makeText(changeInfo.this, "Please enter your phone number.", Toast.LENGTH_LONG).show();
                            binding.editPhoneNumber.setError("Phone number is required.");
                            binding.editPhoneNumber.requestFocus();
                        }
                        else if(binding.editPhoneNumber.getText().toString().length() != 11){
                            Toast.makeText(changeInfo.this, "Please re-enter your phone number.", Toast.LENGTH_LONG).show();
                            binding.editPhoneNumber.setError("Phone number must be 11 digits.");
                            binding.editPhoneNumber.requestFocus();
                        }
                        else if(!matcher.find()){
                            Toast.makeText(changeInfo.this, "Please re-enter your phone number.", Toast.LENGTH_LONG).show();
                            binding.editPhoneNumber.setError("Mobile no. is not valid.");
                            binding.editPhoneNumber.requestFocus();
                        }
                        else{
                            //create hashmap for the data
                            HashMap<String, Object> userMap = new HashMap();
                            userMap.put("phonenum", binding.editPhoneNumber.getText().toString());

                            //update database
                            databaseReference.child(firebaseUser.getUid()).updateChildren(userMap);

                            Toast.makeText(changeInfo.this, "Phone number successfully updated!.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        break;
                    case "birthdate":
                        if(binding.editBirthdate.getText().toString().isEmpty()){
                            Toast.makeText(changeInfo.this, "Please enter your date of birth.", Toast.LENGTH_LONG).show();
                            binding.editBirthdate.setError("Date of birth is required.");
                            binding.editBirthdate.requestFocus();
                        }
                        else{
                            //create hashmap for the data
                            HashMap<String, Object> userMap = new HashMap();
                            userMap.put("dobirth", binding.editBirthdate.getText().toString());

                            //update database
                            databaseReference.child(firebaseUser.getUid()).updateChildren(userMap);

                            Toast.makeText(changeInfo.this, "Birthdate successfully updated!.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        break;
                    case "address":
                        if(binding.editAddress.getText().toString().isEmpty()){
                            Toast.makeText(changeInfo.this, "Please enter your address.", Toast.LENGTH_LONG).show();
                            binding.editAddress.setError("Postal Address is required.");
                            binding.editAddress.requestFocus();
                        }
                        else{
                            //create hashmap for the data
                            HashMap<String, Object> userMap = new HashMap();
                            userMap.put("address", binding.editAddress.getText().toString());

                            //update database
                            databaseReference.child(firebaseUser.getUid()).updateChildren(userMap);

                            Toast.makeText(changeInfo.this, "Address successfully updated!.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        break;
                    default:
                        Toast.makeText(changeInfo.this, "Something went wrong! Please try again later.", Toast.LENGTH_LONG).show();
                        finish();
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(changeInfo.this, "Something went wrong! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }
}