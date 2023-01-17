package com.example.recom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.EventDay;
import com.example.recom.databinding.ActivityPscalendarBinding;
import com.google.android.material.datepicker.DayViewDecorator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class PSCalendar extends AppCompatActivity {
    private ActivityPscalendarBinding binding;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference eventsRef = database.getReference("events");
    private List<Event> events = new ArrayList<>();
    private List<Event> upcoming = new ArrayList<>();
    private List<EventDay> selectedDays = new ArrayList<>();
    private RecyclerView showEvents;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onStart() {
        super.onStart();
        //disable past dates
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        binding.calendarView.setMinimumDate(today);

        //set the calendarview to this month only
        Calendar lastDay = Calendar.getInstance();
        lastDay.set(Calendar.DAY_OF_MONTH, lastDay.getActualMaximum(Calendar.DAY_OF_MONTH));
        binding.calendarView.setMaximumDate(lastDay);

        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    events.add(event);
                }

                for (Event event : events) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", new Locale("en", "PH"));
                    try {
                        Date eventDate = sdf.parse(event.getDate());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(eventDate);
                        selectedDays.add(new EventDay(calendar, R.drawable.baseline_event_24));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                binding.calendarView.setEvents(selectedDays);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPscalendarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        showEvents = binding.showEvents;
        linearLayoutManager = new LinearLayoutManager(this);
        showEvents.setHasFixedSize(true);
        showEvents.setLayoutManager(linearLayoutManager);
        UpcomingEventsAdapter adapter = new UpcomingEventsAdapter(upcoming);

        //get current date and the end of the month day
        Calendar now = Calendar.getInstance();
        TimeZone phTimeZone = TimeZone.getTimeZone("Asia/Manila");
        now.setTimeZone(phTimeZone);
        long startTimestamp = now.getTimeInMillis();
        now.set(Calendar.DAY_OF_MONTH, now.getActualMaximum(Calendar.DAY_OF_MONTH));
        long endTimestamp = now.getTimeInMillis();

        Query query = eventsRef.orderByChild("timestampDate").startAt(startTimestamp).endAt(endTimestamp).limitToFirst(3);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                upcoming.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    showEvents.setAdapter(adapter);
                    Event event = childSnapshot.getValue(Event.class);
                    upcoming.add(event);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        binding.calendarView.setOnDayClickListener(eventDay -> {
            // Get the selected event
            Event selectedEvent = getEventByDate(eventDay.getCalendar().getTime(), events);
            if(selectedEvent != null){
                // go to day calendar and see the events that day
            }
            else{
                Toast.makeText(PSCalendar.this, "Selected day is empty.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private Event getEventByDate(Date date, List<Event> events) {
        Calendar dateEvent = Calendar.getInstance();
        dateEvent.setTime(date);
        for (Event event : events) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", new Locale("en", "PH"));
            try {
                Date eventDate = sdf.parse(event.getDate());
                Calendar eventCalendar = Calendar.getInstance();
                eventCalendar.setTime(eventDate);
                if (eventCalendar.get(Calendar.YEAR) == dateEvent.get(Calendar.YEAR)
                        && eventCalendar.get(Calendar.MONTH) == dateEvent.get(Calendar.MONTH)
                        && eventCalendar.get(Calendar.DAY_OF_MONTH) == dateEvent.get(Calendar.DAY_OF_MONTH)) {
                    return event;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return null;
    }
}