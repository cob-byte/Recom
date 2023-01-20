package com.example.recom;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.recom.databinding.FragmentAcceptScheduleBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class acceptschedule extends Fragment {
    private FragmentAcceptScheduleBinding binding;
    private acceptScheduleAdapter myAdapter;
    private RecyclerView scheduleList;
    private ArrayList<Event> list;
    private ArrayList<String> pushKey;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        binding = FragmentAcceptScheduleBinding.inflate(inflater, container, false);

        reference = database.getReference("pendingSched");
        scheduleList = binding.scheduleList;
        scheduleList.setHasFixedSize(true);
        scheduleList.setLayoutManager(new LinearLayoutManager(requireContext()));
        list = new ArrayList<Event>();
        pushKey = new ArrayList<String>();
        myAdapter = new acceptScheduleAdapter(list, pushKey);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    scheduleList.setAdapter(myAdapter);
                    Event event = dataSnapshot.getValue(Event.class);
                    list.add(event);
                    pushKey.add(dataSnapshot.getKey());
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
            }
        });

        binding.Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });


        return binding.getRoot();
    }
}