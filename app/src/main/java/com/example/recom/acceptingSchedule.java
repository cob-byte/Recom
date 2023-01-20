package com.example.recom;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.recom.databinding.FragmentAcceptScheduleBinding;
import com.example.recom.databinding.FragmentAcceptingScheduleBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class acceptingSchedule extends Fragment {
    private FragmentAcceptingScheduleBinding binding;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference eventsRef = database.getReference("pendingSched");

    public acceptingSchedule() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        binding = FragmentAcceptingScheduleBinding.inflate(inflater, container, false);

        String pushKey = getArguments().getString("pushkey");

        eventsRef.child(pushKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);
                binding.textBname.setText(event.getBorrowName());
                if(event.isBasketballCourt()){
                    binding.facilityBasketball.setVisibility(View.VISIBLE);
                    binding.facilityBasketballText.setText("Basketball Court");
                }
                if(event.isEventHall()){
                    binding.facilityEventHall.setVisibility(View.VISIBLE);
                    binding.facilityEventHallText.setText("Event Hall");
                }
                if(event.getChairs() > 0){
                    binding.facilityChair.setVisibility(View.VISIBLE);
                    binding.facilityChairText.setText("Chairs");
                    binding.facilityChairQuantity.setText(event.getChairs());
                }
                if(event.getTables() > 0){
                    binding.facilityTable.setVisibility(View.VISIBLE);
                    binding.facilityTableText.setText("Tables");
                    binding.facilityTableQuantity.setText(event.getTables());
                }
                if(event.getTents() > 0){
                    binding.facilityTent.setVisibility(View.VISIBLE);
                    binding.facilityTentText.setText("Tents");
                    binding.facilityTableQuantity.setText(event.getTents());
                }
                binding.TextDate.setText(event.getDate());
                binding.TextTime.setText(event.getStartTime() + " - " + event.getEndTime());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(requireActivity())
                    .setMessage("Are you sure you want to accept this schedule?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            eventsRef.child(pushKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Event event = snapshot.getValue(Event.class);
                                    DatabaseReference reference = database.getReference("events").child(pushKey);
                                    reference.setValue(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            eventsRef.child(pushKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(requireContext(), "Schedule has been accepted.", Toast.LENGTH_SHORT).show();
                                                    getParentFragmentManager().popBackStack();
                                                }
                                            });
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(requireContext(), "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .setNegativeButton("No",null)
                    .show();
            }
        });

        binding.Decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(requireActivity())
                    .setMessage("Are you sure you want to accept this schedule?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            eventsRef.child(pushKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(requireContext(), "Schedule has been declined.", Toast.LENGTH_SHORT).show();
                                    getParentFragmentManager().popBackStack();
                                }
                            });
                        }
                    })
                    .setNegativeButton("No",null)
                    .show();
            }
        });

        binding.btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
        return binding.getRoot();
    }
}