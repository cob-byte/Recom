package com.example.recom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.recom.databinding.ActivityPaSchedBinding;

public class PaSched extends AppCompatActivity {
    private ActivityPaSchedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaSchedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        binding.Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.CalendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent calendar = new Intent(PaSched.this, PSCalendar.class);
                calendar.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(calendar);
            }
        });

        binding.ScheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent schedule = new Intent(PaSched.this, AppointmentSchedule.class);
                schedule.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(schedule);
            }
        });
    }
}