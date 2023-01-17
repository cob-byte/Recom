package com.example.recom;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Home extends Fragment {
    private ImageButton cc, ps;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private ViewPager2 viewPager2;
    private ArrayList<wList> pagerArrayList;
    private ImageButton safe;

    public Home() {
        // Required empty public constructor
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