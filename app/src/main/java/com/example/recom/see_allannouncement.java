package com.example.recom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.recom.databinding.ActivitySeeAllannouncementBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class see_allannouncement extends AppCompatActivity {
    private ActivitySeeAllannouncementBinding binding;
    private seeAllAnnouncementAdapter myAdapter;
    private RecyclerView announcementList;
    private ArrayList<Announcement> list;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeeAllannouncementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        reference = database.getReference("Announcement");
        announcementList = binding.seeAllPoll;
        announcementList.setHasFixedSize(true);
        announcementList.setLayoutManager(new LinearLayoutManager(see_allannouncement.this));
        list = new ArrayList<Announcement>();
        myAdapter = new seeAllAnnouncementAdapter(list);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    announcementList.setAdapter(myAdapter);
                    Announcement announcement = dataSnapshot.getValue(Announcement.class);
                    list.add(announcement);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(see_allannouncement.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
            }
        });


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}