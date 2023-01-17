package com.example.recom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.recom.databinding.ActivityHealthServicesBinding;
import com.example.recom.databinding.ActivityHotlineAllnumBinding;
import com.example.recom.databinding.ActivityMyPollCcBinding;

import java.util.ArrayList;
import java.util.List;

public class hotline_allnum extends AppCompatActivity {

    //Recycler View
    HotlineInfo hotlineinfo;
    HotlineAllNumAdapter hotlineAllNumAdapter;
    private RecyclerView recyclerView;
    private List<HotlineInfo> hotlineInfos;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotline_allnum);

        recyclerView=findViewById(R.id.Tphone_recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        hotlineInfos = new ArrayList<>();

        hotlineinfo=new HotlineInfo("Manila Doctors","0999999999");
        hotlineInfos.add(hotlineinfo);
        hotlineinfo=new HotlineInfo("Manila Med","091111111");
        hotlineInfos.add(hotlineinfo);
        hotlineinfo=new HotlineInfo("Manila Mo","092222222");
        hotlineInfos.add(hotlineinfo);
        hotlineinfo=new HotlineInfo("Manila Me","0933333333");
        hotlineInfos.add(hotlineinfo);

        hotlineAllNumAdapter= new HotlineAllNumAdapter(hotline_allnum.this, hotlineInfos);
        recyclerView.setAdapter(hotlineAllNumAdapter);









































//        binding = ActivityHealthServicesBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

        //recyclerView=findViewById(R.id.recyclerView);


//        InitializeCardview();
//
//        binding.Backbtn.setOnClickListener(new View.OnClickListener() {
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