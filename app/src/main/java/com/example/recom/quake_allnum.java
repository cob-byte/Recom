package com.example.recom;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class quake_allnum extends AppCompatActivity {

    //Recycler View
    HotlineInfo hotlineinfo;
    HotlineAllNumAdapter hotlineAllNumAdapter;
    private RecyclerView recyclerView;
    private List<HotlineInfo> hotlineInfos;
    private RecyclerView mhotline_recycler;
    private List<HotlineInfo> mhotlineInfos;
    private ImageButton Backbtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake_services);

        getSupportActionBar().hide();

        Backbtn = findViewById(R.id.Backbtn);
        Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.QuakeTeleRecycler);
        mhotline_recycler = findViewById(R.id.QuakeMobileRecycler);

//---  EARTHQUAKE SERVICES ----
//TelephoneNumbers
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

// Get the arrays from the string.xml file
        String[] tnames = getResources().getStringArray(R.array.qthotline_names);
        String[] tnumbers = getResources().getStringArray(R.array.qthotline_numbers);

        hotlineInfos = new ArrayList<>();

// Iterate through the arrays, creating a new HotlineInfo object for each element and adding it to the hotlineInfos list
        for (int i = 0; i < tnames.length; i++) {
            hotlineinfo = new HotlineInfo(tnames[i], tnumbers[i]);
            hotlineInfos.add(hotlineinfo);
        }

        hotlineAllNumAdapter = new HotlineAllNumAdapter(quake_allnum.this, hotlineInfos);
        recyclerView.setAdapter(hotlineAllNumAdapter);

//MobileNumbers
        LinearLayoutManager mlinearLayoutManager = new LinearLayoutManager(this);
        mlinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mhotline_recycler.setLayoutManager(mlinearLayoutManager);

        // Get the arrays from the string.xml file
        String[] mnames = getResources().getStringArray(R.array.qmhotline_names);
        String[] mnumbers = getResources().getStringArray(R.array.qmhotline_numbers);

        mhotlineInfos = new ArrayList<>();

        // Iterate through the arrays, creating a new HotlineInfo object for each element and adding it to the hotlineInfos list
        for (int i = 0; i < mnames.length; i++) {
            hotlineinfo = new HotlineInfo(mnames[i], mnumbers[i]);
            mhotlineInfos.add(hotlineinfo);
        }

        HotlineMobileNumAdapter hotlineMobileNumAdapter = new HotlineMobileNumAdapter(quake_allnum.this, mhotlineInfos);
        mhotline_recycler.setAdapter(hotlineMobileNumAdapter);

//---  END OF EARTHQUAKE SERVICES ----





































//        binding = ActivityHealthServicesBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

        //recyclerView=findViewById(R.id.recyclerView);


//        InitializeCardview();
//
//       binding.Backbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view){
//                Intent back= new Intent(hotline_allnum.this, SafeMe.class );
//                startActivity(back);
//            }
//        });
//    }
//
//    private void InitializeCardview() {
//        recyclerView= findViewById(R.id.TelPhone_recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        hotlineArrayList= new ArrayList<>();
//
//        adapter=new HotlineAllNumAdapter(this, hotlineArrayList);
//        recyclerView.setAdapter(adapter);
//
//        CreateListData();
//    }
//
//    private void CreateListData() {
//        HotlineInfo info =new HotlineInfo("Manila Medical", 65 );
//        hotlineArrayList.add(info);
//
//        info=new HotlineInfo("Jacob Barcelona", 21);
//        hotlineArrayList.add(info);
    }
}