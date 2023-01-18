package com.example.recom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;

import com.example.recom.databinding.ActivityAllSchedBinding;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.github.tibolte.agendacalendarview.render.DefaultEventRenderer;
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
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class all_sched extends AppCompatActivity implements CalendarPickerController {
    private ActivityAllSchedBinding binding;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference eventsRef = database.getReference("events");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllSchedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        //initialize event list
        List<CalendarEvent> eventList = new ArrayList<>();


        Calendar now = Calendar.getInstance();
        TimeZone phTimeZone = TimeZone.getTimeZone("Asia/Manila");
        now.setTimeZone(phTimeZone);
        long startTimestamp = now.getTimeInMillis();
        now.set(Calendar.DAY_OF_MONTH, now.getActualMaximum(Calendar.DAY_OF_MONTH));
        long endTimestamp = now.getTimeInMillis();

        Query query = eventsRef.orderByChild("timestampDate").startAt(startTimestamp).endAt(endTimestamp);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventList.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Event event = childSnapshot.getValue(Event.class);
                    String date = event.getDate();
                    String startTime = event.getStartTime();
                    String endTime = event.getEndTime();
                    String startDateTime = date+" "+startTime;
                    String endDateTime = date+" "+endTime;

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", new Locale("en", "PH"));

                    Calendar startCalendar = Calendar.getInstance();
                    Calendar endCalendar = Calendar.getInstance();
                    try {
                        startCalendar.setTime(sdf.parse(startDateTime));
                        endCalendar.setTime(sdf.parse(endDateTime));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    BaseCalendarEvent calendarEvent = new BaseCalendarEvent(event.getName(), event.getDescription(), "",
                            ContextCompat.getColor(all_sched.this, R.color.blue), startCalendar, endCalendar, true);
                    eventList.add(calendarEvent);
                }

                // Get today's date
                Calendar today = Calendar.getInstance();
                //get last day of the year
                Calendar endOfYear = Calendar.getInstance();
                endOfYear.set(Calendar.MONTH, Calendar.DECEMBER);
                endOfYear.set(Calendar.DAY_OF_MONTH, 31);

                binding.agendaCalendarView.init(eventList, today, endOfYear, new Locale("en", "PH"), all_sched.this);

                binding.agendaCalendarView.addEventRenderer(new DefaultEventRenderer());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onDaySelected(DayItem dayItem) {

    }

    @Override
    public void onEventSelected(CalendarEvent event) {

    }

    @Override
    public void onScrollToDate(Calendar calendar) {

    }
}