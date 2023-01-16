package com.example.recom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.recom.databinding.ActivityPscalendarBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PSCalendar extends AppCompatActivity {
    private ActivityPscalendarBinding binding;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference eventsRef = database.getReference("events");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPscalendarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        //disable past dates
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        binding.calendarView.setMinimumDate(today);

        List<Event> events = new ArrayList<>();
        List<Calendar> disabledDays = new ArrayList<>();
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
                        disabledDays.add(calendar);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                binding.calendarView.setDisabledDays(disabledDays);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

        binding.calendarView.setOnDayClickListener(eventDay -> {
            // Get the selected event
            Event selectedEvent = getEventByDate(eventDay.getCalendar().getTime(), events);
            // Update the recycler view with the selected event's details
            updateRecyclerView(selectedEvent);
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void updateRecyclerView(Event selectedEvent) {
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