package com.example.recom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.recom.databinding.ActivityEditProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class EditProfile extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private final FirebaseAuth firebaseProfile = FirebaseAuth.getInstance();
    private final FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        Intent intent = getIntent();
        binding.fullnameTop.setText(intent.getStringExtra("fname").toString());
        binding.emailTop.setText(intent.getStringExtra("email").toString());
        if(intent.getStringExtra("photo") != null){
            Uri uriImage = Uri.parse(intent.getStringExtra("photo").toString());
            Picasso.get().load(uriImage).into(binding.profileImage);
        }

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
                        setProfileInfo(phone, birthdate, address);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        binding.editFullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = "fullname";
                Intent fullName = new Intent(EditProfile.this, changeInfo.class);
                fullName.putExtra("type", type);
                fullName.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(fullName);
            }
        });

        binding.editEmailAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = "email";
                Intent emailAdd = new Intent(EditProfile.this, changeInfo.class);
                emailAdd.putExtra("type", type);
                emailAdd.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(emailAdd);
            }
        });

        binding.editphoneNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = "phone";
                Intent phoneNum = new Intent(EditProfile.this, changeInfo.class);
                phoneNum.putExtra("type", type);
                phoneNum.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(phoneNum);
            }
        });

        binding.editBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = "birthdate";
                Intent BirthDate = new Intent(EditProfile.this, changeInfo.class);
                BirthDate.putExtra("type", type);
                BirthDate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(BirthDate);
            }
        });

        binding.editAddr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = "address";
                Intent address = new Intent(EditProfile.this, changeInfo.class);
                address.putExtra("type", type);
                address.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(address);
            }
        });

        binding.btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setProfileInfo(String phone, String birthdate, String address) {
        binding.textFullName.setText(firebaseUser.getDisplayName());
        binding.textEmail.setText(firebaseUser.getEmail());
        binding.textPhone.setText(phone);
        binding.textBirthdate.setText(birthdate);
        binding.textAddress.setText(address);
    }
}