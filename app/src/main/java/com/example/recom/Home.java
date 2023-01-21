package com.example.recom;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Home extends Fragment {
    private ImageButton cc, ps, safe;
    private TextView seemore;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private final DatabaseReference eventsRef = database.getReference("Announcement");
    private ViewPager2 viewPager2;
    private ArrayList<Announcement> announcement;
    private FloatingActionButton menu1, menu2, menu3, menu4;
    private FloatingActionMenu menu;

    public Home() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        viewPager2 = view.findViewById(R.id.view_pager2);
        announcement = new ArrayList<>();
        viewPagerAdapter viewPagerAdapter = new viewPagerAdapter(announcement);
        viewPager2.setAdapter(viewPagerAdapter);
        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                announcement.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(dataSnapshot != null) {
                        Announcement announcement1 = dataSnapshot.getValue(Announcement.class);
                        announcement.add(announcement1);
                    }
                }
                viewPagerAdapter.setData(announcement);
                viewPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
            }
        });

        viewPager2.setClipToPadding(false);

        viewPager2.setClipChildren(false);

        viewPager2.setOffscreenPageLimit(2);

        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        ps = view.findViewById(R.id.paschedBtn);
        ps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pasched = new Intent(requireActivity(), PaSched.class);
                pasched.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(pasched);
                Intent loadingScreenIntent = new Intent(requireActivity(), LoadScreen.class);
                startActivity(loadingScreenIntent);
            }
        });

        safe=view.findViewById(R.id.safemeBtn);
        safe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent safe = new Intent(requireActivity(), SafeMe.class);
                safe.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(safe);
                Intent loadingScreenIntent = new Intent(requireActivity(), LoadScreen.class);
                startActivity(loadingScreenIntent);
            }
        });

        menu = view.findViewById(R.id.menu);

        databaseReference = database.getReference("Users");
        databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user.userRole == 2)
                {
                    menu.setVisibility(View.VISIBLE);
                    menu1 = view.findViewById(R.id.menuAS);
                    menu1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frame_layout, new acceptschedule());
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    });

                    menu2 = view.findViewById(R.id.menuABP);
                    menu2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent addBarangayPoll = new Intent(requireActivity(), barangayConsensus.class);
                            addBarangayPoll.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(addBarangayPoll);
                        }
                    });

                    menu3 = view.findViewById(R.id.menuAA);
                    menu3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent addAnnouncement = new Intent(requireActivity(), Add_Announcement.class);
                            addAnnouncement.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(addAnnouncement);
                        }
                    });

                    menu4 = view.findViewById(R.id.menuVU);
                    menu4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frame_layout, new verifyuser());
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    });
                }
                else{
                    menu.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        cc = view.findViewById(R.id.ccBtn);
        cc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent consensus = new Intent(requireActivity(), CommunityConsensus.class);
                consensus.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(consensus);
                Intent loadingScreenIntent = new Intent(requireActivity(), LoadScreen.class);
                startActivity(loadingScreenIntent);
            }
        });

        seemore = view.findViewById(R.id.seeMore);

        seemore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent consensus = new Intent(requireActivity(), see_allannouncement.class);
                consensus.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(consensus);
            }
        });

        return view;
    }
}