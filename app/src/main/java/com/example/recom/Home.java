package com.example.recom;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Home extends Fragment {
    private ImageButton cc, ps, safe;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private final DatabaseReference eventsRef = database.getReference("events");
    private ViewPager2 viewPager2;
    private ArrayList<wList> pagerArrayList;
    private FloatingActionButton menu1, menu2, menu3, menu4;
    private FloatingActionMenu menu;

    public Home() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        //delete past events
        // Create a query to retrieve events whose date is in the past
        //get current date
        Calendar currentDatePH = Calendar.getInstance();
        currentDatePH.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));
        currentDatePH.set(Calendar.HOUR_OF_DAY, 0);
        currentDatePH.set(Calendar.MINUTE, 0);
        currentDatePH.set(Calendar.SECOND, 0);
        currentDatePH.set(Calendar.MILLISECOND, 0);
        Query pastEventsQuery = eventsRef.orderByChild("date").endAt(currentDatePH.getTimeInMillis());

        // Attach a listener to the query
        pastEventsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    // Get the event key
                    String eventKey = eventSnapshot.getKey();

                    // Delete the event from the database
                    eventsRef.child(eventKey).setValue(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        viewPager2 = view.findViewById(R.id.view_pager2);

        int[] images = {R.drawable.pasuyo,R.drawable.pasched,R.drawable.cc,R.drawable.safeme,R.drawable.tararecomlogo};
        String[] heading = {"Jaymark","Grilled","Dessert","Italian","Shakes"};
        String[] desc = {getString(R.string.a_desc),
                getString(R.string.b_desc),
                getString(R.string.c_desc),
                getString(R.string.d_desc)
                ,getString(R.string.e_desc)};

        pagerArrayList = new ArrayList<>();

        for (int i =0; i< images.length ; i++){

            wList viewPagerItem = new wList(images[i],heading[i],desc[i]);
            pagerArrayList.add(viewPagerItem);

        }

        viewPagerAdapter viewPagerAdapter = new viewPagerAdapter(pagerArrayList);

        viewPager2.setAdapter(viewPagerAdapter);

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
            }
        });

        safe=view.findViewById(R.id.safemeBtn);
        safe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent safe = new Intent(requireActivity(), SafeMe.class);
                safe.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(safe);
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
                            Intent acceptSchedule = new Intent(requireActivity(), CommunityConsensus.class);
                            acceptSchedule.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(acceptSchedule);
                        }
                    });

                    menu2 = view.findViewById(R.id.menuABP);
                    menu2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent addBarangayPoll = new Intent(requireActivity(), PaSched.class);
                            addBarangayPoll.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(addBarangayPoll);
                        }
                    });

                    menu3 = view.findViewById(R.id.menuAA);
                    menu3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent addAnnouncement = new Intent(requireActivity(), SafeMe.class);
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
            }
        });

        return view;
    }
}