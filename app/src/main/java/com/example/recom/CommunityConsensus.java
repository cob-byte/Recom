package com.example.recom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.recom.databinding.ActivityCommunityConsensusBinding;

public class CommunityConsensus extends AppCompatActivity {
    private ActivityCommunityConsensusBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommunityConsensusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        binding.btnAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ask = new Intent(CommunityConsensus.this, AskQuestion.class);
                ask.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ask);
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}