package com.example.recom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.recom.databinding.ActivitySettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Set;

public class Settings extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    private final FirebaseAuth firebaseProfile = FirebaseAuth.getInstance();
    private final FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();

    @Override
    protected void onStart() {
        if(firebaseUser != null){
            Intent intent = getIntent();
            binding.accSettingsFname.setText(firebaseUser.getDisplayName());
            binding.accSettingsEmail.setText(firebaseUser.getEmail());
            if(!intent.getStringExtra("photo").toString().equals("none")){
                String image = intent.getStringExtra("photo").toString();
                Uri photo = Uri.parse(image);
                Picasso.get().load(photo).into(binding.profileImage);
            }
        }
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();
        Intent intent = getIntent();

        binding.btnBackSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editProfile = new Intent(Settings.this, EditProfile.class);
                if(!intent.getStringExtra("photo").toString().equals("none")){
                    editProfile.putExtra("photo", intent.getStringExtra("photo"));
                }
                editProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(editProfile);
            }
        });

        binding.changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changePassword = new Intent(Settings.this, ChangePassword.class);
                changePassword.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(changePassword);
            }
        });

        binding.btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseProfile.signOut();
                Toast.makeText(Settings.this, "Logged Out", Toast.LENGTH_LONG).show();
                Intent logout = new Intent(Settings.this, MainActivity.class);
                logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(logout);
                finish();
            }
        });
    }
}