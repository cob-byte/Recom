package com.example.recom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.recom.databinding.ActivityMyPollCcBinding;
import com.example.recom.databinding.ActivityPollviewCcBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyPoll_cc extends AppCompatActivity {
    private ActivityMyPollCcBinding binding;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth firebaseProfile = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();
    private DatabaseReference reference;
    private allPollsAdapter myAdapter;
    private RecyclerView myPollList;
    private ArrayList<cConsensus> myList;
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
        binding = ActivityMyPollCcBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        myPollList = binding.seeMyPoll;
        reference = database.getReference("communityConsensus").child("questions");
        myPollList.setHasFixedSize(true);
        myPollList.setLayoutManager(new LinearLayoutManager(this));
        myList = new ArrayList<cConsensus>();
        pushKey = new ArrayList<String>();
        myAdapter = new allPollsAdapter(this, myList, pushKey);

        listener = reference.orderByChild("author").equalTo(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myList.clear();
                pushKey.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    myPollList.setAdapter(myAdapter);
                    cConsensus consensus = dataSnapshot.getValue(cConsensus.class);
                    myList.add(consensus);
                    pushKey.add(dataSnapshot.getKey());
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyPoll_cc.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
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