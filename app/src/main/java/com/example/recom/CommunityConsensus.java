package com.example.recom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class CommunityConsensus extends AppCompatActivity {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference, reference1;
    private cConsensusAdapter myAdapter;
    private bConsensusAdapter myAdapter1;
    private RecyclerView queList, brgyList;
    private ArrayList<cConsensus> list;
    private ArrayList<bConsensus> pList;
    private ArrayList<String> pushKey, pushKey1;
    private ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_consensus);
        Button btnAsk = (Button) findViewById(R.id.btnAsk);
        Button brgySeeAllPoll = (Button) findViewById(R.id.SeeAll);
        Button communitySeeAllPoll = (Button) findViewById(R.id.SeeAll2);
        Button seeMyPoll = (Button) findViewById(R.id.btnPoll);
        ImageButton backBtn = (ImageButton) findViewById(R.id.backBtn);

        getSupportActionBar().hide();
        //barangay consensus
        reference1 = database.getReference("barangayConsensus").child("questions");
        brgyList = findViewById(R.id.barangayPollList);
        brgyList.setHasFixedSize(true);
        brgyList.setLayoutManager(new LinearLayoutManager(this));
        pList = new ArrayList<bConsensus>();
        pushKey1 = new ArrayList<String>();
        myAdapter1 = new bConsensusAdapter(this, pList, pushKey1);
        listener = reference1.limitToFirst(3).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pList.clear();
                pushKey1.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    brgyList.setAdapter(myAdapter1);
                    bConsensus bconsensus = dataSnapshot.getValue(bConsensus.class);
                    pList.add(bconsensus);
                    pushKey1.add(dataSnapshot.getKey());
                }
                myAdapter1.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CommunityConsensus.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
            }
        });

        //community consensus
        reference = database.getReference("communityConsensus").child("questions");
        queList = findViewById(R.id.questionList);
        queList.setHasFixedSize(true);
        queList.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<cConsensus>();
        pushKey = new ArrayList<String>();
        myAdapter = new cConsensusAdapter(this, list, pushKey);

        listener = reference.orderByChild("vote").limitToFirst(3).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                pushKey.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    queList.setAdapter(myAdapter);
                    cConsensus consensus = dataSnapshot.getValue(cConsensus.class);
                    list.add(consensus);
                    pushKey.add(dataSnapshot.getKey());
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CommunityConsensus.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
            }
        });

        btnAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ask = new Intent(CommunityConsensus.this, AskQuestion.class);
                ask.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ask);
            }
        });

        seeMyPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent seeMyPoll = new Intent(CommunityConsensus.this, MyPoll_cc.class);
                seeMyPoll.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(seeMyPoll);
            }
        });

        brgySeeAllPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bSeeAllPoll = new Intent(CommunityConsensus.this, bcAllPoll.class);
                bSeeAllPoll.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(bSeeAllPoll);
            }
        });

        communitySeeAllPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cSeeAllPoll = new Intent(CommunityConsensus.this, pollview_cc.class);
                cSeeAllPoll.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(cSeeAllPoll);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reference != null && listener != null) {
                    reference.removeEventListener(listener);
                }
                finish();
            }
        });
    }
}