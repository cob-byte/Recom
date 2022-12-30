package com.example.recom;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.example.recom.databinding.ActivityDashboardBinding;
import com.example.recom.databinding.ActivityIntroBinding;
import com.example.recom.databinding.IntroLayoutBinding;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class activityIntro extends AppCompatActivity {

    private ActivityIntroBinding binding;
    private introPagerAdapter IntroPagerAdapter;
    private int position = 0 ;
    private Animation btnAnim ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(binding.getRoot());

        // make the activity on full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // when this activity is about to be launch we need to check if its opened before or not
        if (restorePrefData()) {
            Intent mainActivity = new Intent(getApplicationContext(),MainActivity.class );
            startActivity(mainActivity);
            finish();
        }

        // hide the action bar
        getSupportActionBar().hide();

        // anim
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);

        // fill list screen
        final List<itemIntro> iList = new ArrayList<>();
        iList.add(new itemIntro("WELCOME!","ReCom or Re-Communicate is a sector based app which aims to help a community to provide good communication inside and outside the vicinity. This app is to make communication at ease for the betterment of their environment.",R.drawable.tararecomlogo));
        iList.add(new itemIntro("Community Consensus","Lists of the latest Projects for the barangay and important events that will occur in a period of time and may affect the community will be highlighted and posted for transparency and awareness of all. Hence, a voting system is included for the betterment of the sector, barangay officials, and its citizens. ",R.drawable.cc));
        iList.add(new itemIntro("Pasuyo","A service that gives a chance to people who cannot do such activity at the time needed. Fill in exact descriptions to specify one's \"pasuyo\" and the type of service that shall be done, given the date and time. For the improvement of the system, feedbacks are available after the service is completed. Both \"nakikisuyo\" and the \"susuyo\" will benefit as the \"susuyo\" will obtain points that may benefit one in the future.",R.drawable.pasuyo));
        iList.add(new itemIntro("Safe Me!","Safety is one of our top priorities. In this section Safe Me!lists of hospitals, evacuation centers, contact numbers, and its details of the accessible and nearest emergency organizations/institutions are posted for future references. Safe Me! or Emergency and Disaster Management is a must for a faster response in such unforeseen events.",R.drawable.safeme));
        iList.add(new itemIntro("Pa-Sched","This feature is dedicated for the inquiries and scheduling for accessing the barangay facilities and its properties. The time and date should be specified as well as the user’s information.",R.drawable.pasched));


        // setup viewpager
        IntroPagerAdapter = new introPagerAdapter(this,iList);
        binding.sViewpager.setAdapter(IntroPagerAdapter);

        // setup tablayout with viewpager
        binding.tIndicator.setupWithViewPager(binding.sViewpager);

        // next button click Listener

        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position = binding.sViewpager.getCurrentItem();
                if (position < iList.size()) {

                    position++;
                    binding.sViewpager.setCurrentItem(position);


                }

                if (position == iList.size()-1) { // when we reach to the last screen
                    //show the GETSTARTED Button and hide the indicator and the next button
                    loadLastScreen();
                }

            }
        });

        // tablayout add change listener
        binding.tIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == iList.size()-1) {

                    loadLastScreen();

                }

                if (tab.getPosition() < iList.size()-1 && binding.gStarted.getVisibility() == View.VISIBLE){
                    binding.next.setVisibility(View.VISIBLE);
                    binding.gStarted.setVisibility(View.INVISIBLE);
                    binding.skip.setVisibility(View.VISIBLE);
                    binding.tIndicator.setVisibility(View.VISIBLE);

                    binding.next.setAnimation(btnAnim);
                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        // Get Started button click listener

        binding.gStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open main activity

                Intent mainActivity = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(mainActivity);
                // also we need to save a boolean value to storage so next time when the user run the app
                // we could know that he is already checked the intro screen activity
                // i'm going to use shared preferences to that process
                savePrefsData();
                finish();
            }
        });

        // skip button click listener

        binding.skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.sViewpager.setCurrentItem(iList.size());
            }
        });



    }

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isIntroActivityOpenedBefore = pref.getBoolean("isIntroOpened",false);
        return  isIntroActivityOpenedBefore;
    }

    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpened",true);
        editor.commit();
    }

    // show the GETSTARTED Button and hide the indicator and the next button
    private void loadLastScreen() {
        binding.next.setVisibility(View.INVISIBLE);
        binding.gStarted.setVisibility(View.VISIBLE);
        binding.skip.setVisibility(View.INVISIBLE);
        binding.tIndicator.setVisibility(View.INVISIBLE);
        // TODO : ADD an animation the getstarted button
        // setup animation
        binding.gStarted.setAnimation(btnAnim);
    }
}