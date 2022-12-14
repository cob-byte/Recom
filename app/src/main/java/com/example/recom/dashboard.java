package com.example.recom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class dashboard extends AppCompatActivity {

    private Button tempsignout;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private ViewPager2 viewPager2;
    private ArrayList<wList> pagerArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tempsignout = findViewById(R.id.signouttemp);
        viewPager2 = findViewById(R.id.view_pager2);

        tempsignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(dashboard.this, MainActivity.class));
                finish();
            }
        });

        int[] images = {R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e};
        String[] heading = {"Baked","Grilled","Dessert","Italian","Shakes"};
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
    }
}