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

import com.example.recom.databinding.FragmentVerifyuserBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class verifyuser extends Fragment {

    private FragmentVerifyuserBinding binding;
    private listVerifyAdapter myAdapter;
    private RecyclerView userList;
    private ArrayList<verifyInfo> list;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference;

    public verifyuser() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        binding = FragmentVerifyuserBinding.inflate(inflater, container, false);

        reference = database.getReference("pendingVerify");
        userList = binding.listUser;
        userList.setHasFixedSize(true);
        userList.setLayoutManager(new LinearLayoutManager(requireContext()));
        list = new ArrayList<verifyInfo>();
        myAdapter = new listVerifyAdapter(list);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    userList.setAdapter(myAdapter);
                    verifyInfo verifyinfo = dataSnapshot.getValue(verifyInfo.class);
                    list.add(verifyinfo);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
            }
        });

        return binding.getRoot();
    }
}