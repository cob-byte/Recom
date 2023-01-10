package com.example.recom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.example.recom.databinding.ActivityViewpollBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Viewpoll extends AppCompatActivity {
    private ActivityViewpollBinding binding;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewpollBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        Intent intent = getIntent();
        String pushKey = intent.getStringExtra("pushKey");

        reference = database.getReference("communityConsensus").child("questions").child(pushKey);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cConsensus consensus = snapshot.getValue(cConsensus.class);
                if(consensus != null){
                    boolean anon = consensus.getAnon();
                    binding.vPollTitle.setText(consensus.getTitle());
                    if(anon == false){
                        binding.vPollName.setText(consensus.getName());
                    }
                    else{
                        binding.vPollName.setText("Anonymous");
                    }
                    binding.vPollQuestion.setText(consensus.getQuestion());
                    binding.viewanswer1.setText(consensus.getAnswer1());
                    binding.viewanswer2.setText(consensus.getAnswer2());
                    if(consensus.getAnswer3() != null){
                        if(consensus.getAnswer4() != null){
                            binding.vAnswer3.setVisibility(View.VISIBLE);
                            binding.vAnswer4.setVisibility(View.VISIBLE);
                            binding.viewanswer3.setText(consensus.getAnswer3());
                            binding.viewanswer4.setText(consensus.getAnswer4());
                        }
                        else{
                            binding.vAnswer3.setVisibility(View.VISIBLE);
                            binding.viewanswer3.setText(consensus.getAnswer3());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}