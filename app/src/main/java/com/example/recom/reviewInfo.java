package com.example.recom;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.recom.databinding.FragmentReviewInfoBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.HashMap;

public class reviewInfo extends Fragment {
    private FragmentReviewInfoBinding binding;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private final FirebaseAuth firebaseProfile = FirebaseAuth.getInstance();
    private final FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();

    public reviewInfo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        binding = FragmentReviewInfoBinding.inflate(inflater, container, false);

        if(!getArguments().getString("photourl").equals("empty")){
            Picasso.get().load(getArguments().getString("photourl")).into(binding.profileImage2);
        }

        binding.fullnameTop2.setText(getArguments().getString("name"));

        Picasso.get().load(getArguments().getString("votersCert")).into(binding.imageVCert);
        Picasso.get().load(getArguments().getString("brgyCert")).into(binding.imageBCert);

        binding.verifyU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the data from database
                databaseReference = database.getReference("Users");
                databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if(user.userRole == 2){
                            new AlertDialog.Builder(requireActivity())
                                    .setMessage("Are you sure you want to verify this user?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            databaseReference = database.getReference("Users");
                                            databaseReference.child(getArguments().getString("uid")).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    HashMap<String, Object> userMap = new HashMap<>();
                                                    userMap.put("userRole", 1);
                                                    Toast.makeText(requireContext(), "User is now verified", Toast.LENGTH_SHORT).show();
                                                    getParentFragmentManager().popBackStack();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(requireActivity(), "Something went wrong! Please try again later.", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton("No",null)
                                    .show();
                        }
                        else{
                            Toast.makeText(requireContext(), "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        return binding.getRoot();
    }
}