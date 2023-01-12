package com.example.recom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.recom.databinding.ActivityPollviewCcBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class pollview_cc extends AppCompatActivity {
    private ActivityPollviewCcBinding binding;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference;
    private allPollsAdapter myAdapter;
    private RecyclerView allPollList;
    private ArrayList<cConsensus> allList;
    private ArrayList<String> pushKey;
    private ValueEventListener listener;

    @Override
    protected void onStop() {
        super.onStop();
        if (reference != null && listener != null) {
            reference.removeEventListener(listener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPollviewCcBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        allPollList = binding.seeAllPoll;
        reference = database.getReference("communityConsensus").child("questions");
        allPollList.setHasFixedSize(true);
        allPollList.setLayoutManager(new LinearLayoutManager(this));
        allList = new ArrayList<cConsensus>();
        pushKey = new ArrayList<String>();
        myAdapter = new allPollsAdapter(this, allList, pushKey);

        listener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allList.clear();
                pushKey.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    allPollList.setAdapter(myAdapter);
                    cConsensus consensus = dataSnapshot.getValue(cConsensus.class);
                    allList.add(consensus);
                    pushKey.add(dataSnapshot.getKey());
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(pollview_cc.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
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