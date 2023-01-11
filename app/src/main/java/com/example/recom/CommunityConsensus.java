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
    private DatabaseReference reference;
    private cConsensusAdapter myAdapter;
    private RecyclerView queList;
    private ArrayList<cConsensus> list;
    private ArrayList<String> pushKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_consensus);
        Button btnAsk = (Button) findViewById(R.id.btnAsk);
        ImageButton backBtn = (ImageButton) findViewById(R.id.backBtn);

        getSupportActionBar().hide();

        queList = findViewById(R.id.questionList);
        reference = database.getReference("communityConsensus").child("questions");
        queList.setHasFixedSize(true);
        queList.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<cConsensus>();
        pushKey = new ArrayList<String>();
        myAdapter = new cConsensusAdapter(this, list, pushKey);

        reference.limitToFirst(3).addValueEventListener(new ValueEventListener() {
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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}