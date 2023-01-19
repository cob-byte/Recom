package com.example.recom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.recom.databinding.ActivityBcAllPollBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class bcAllPoll extends AppCompatActivity {
    private ActivityBcAllPollBinding binding;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference;
    private bcAllPollAdapter myAdapter;
    private RecyclerView allPollList;
    private ArrayList<bConsensus> allList;
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
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBcAllPollBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        reference = database.getReference("barangayConsensus").child("questions");

        //recyclerview
        allPollList = binding.seeAllPoll;
        allPollList.getItemAnimator().setChangeDuration(0);
        allPollList.setHasFixedSize(true);

        //linearlayout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        allPollList.setLayoutManager(linearLayoutManager);

        //arraylist
        allList = new ArrayList<bConsensus>();
        pushKey = new ArrayList<String>();

        //adapter
        myAdapter = new bcAllPollAdapter(this, allList, pushKey);

        listener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allList.clear();
                pushKey.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    allPollList.setAdapter(myAdapter);
                    bConsensus consensus = dataSnapshot.getValue(bConsensus.class);
                    allList.add(consensus);
                    pushKey.add(dataSnapshot.getKey());
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(bcAllPoll.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
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