package com.example.recom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.recom.databinding.ActivitySettingsBinding;
import com.squareup.picasso.Picasso;

public class Settings extends AppCompatActivity {
    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        Intent intent = getIntent();
        binding.accSettingsFname.setText(intent.getStringExtra("fname").toString());
        binding.accSettingsEmail.setText(intent.getStringExtra("email").toString());
        String image = intent.getStringExtra("photo").toString();
        Uri photo = Uri.parse(image);
        Picasso.get().load(photo).into(binding.profileImage);

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
                editProfile.putExtra("fname", intent.getStringExtra("fname"));
                editProfile.putExtra("email", intent.getStringExtra("email"));
                editProfile.putExtra("photo", intent.getStringExtra("photo"));
                editProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(editProfile);
            }
        });
    }
}