package com.example.recom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.recom.databinding.ActivityWeeklySchedBinding;
import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.model.CalendarEvent;
import devs.mulham.horizontalcalendar.utils.CalendarEventsPredicate;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class weekly_sched extends AppCompatActivity {
    private ActivityWeeklySchedBinding binding;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference eventsRef = database.getReference("events");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeeklySchedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        // retrieve the selected day's date from the intent
        Date selectedDate = (Date) getIntent().getSerializableExtra("selected_day");
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.setTime(selectedDate);

        //get the last day of the month
        Calendar endDate = Calendar.getInstance();
        endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));

        //get the day today
        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);

        // Initialize HorizontalCalendar
        devs.mulham.horizontalcalendar.HorizontalCalendar horizontalCalendar = new devs.mulham.horizontalcalendar.HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate,endDate)
                .datesNumberOnScreen(7)
                .defaultSelectedDate(selectedCalendar)
                .build();


        //set up listener for selected date and events in time table
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                binding.timeTable.removeAll(); // remove all items

                //get date today
                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);

                if (date.before(today)) {
                    Toast.makeText(weekly_sched.this, "You cannot select past date", Toast.LENGTH_SHORT).show();
                    horizontalCalendar.goToday(false);
                } else {
                    // show events in that day
                    //get the selected date and convert it to yyyy-MM-dd format
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String dateString = sdf.format(date.getTime());

                    //query the database for events that match the selected date
                    eventsRef.orderByChild("date").equalTo(dateString).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ArrayList<Schedule> schedules = new ArrayList<Schedule>();
                            for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                                Event event = eventSnapshot.getValue(Event.class);
                                Schedule schedule = new Schedule();
                                schedule.setClassTitle(event.getName()); // sets event name
                                //description
                                schedule.setClassPlace(event.getDescription()); // sets event location
                                //set start time
                                String startTimeString = event.getStartTime();
                                int startHour = Integer.parseInt(startTimeString.substring(0,2));
                                int startMinute = Integer.parseInt(startTimeString.substring(3,5));
                                schedule.setStartTime(new Time(startHour, 0));
                                //set end time
                                String endTimeString = event.getEndTime();
                                int endHour = Integer.parseInt(endTimeString.substring(0,2));
                                int endMinute = Integer.parseInt(endTimeString.substring(3,5));
                                schedule.setEndTime(new Time(endHour, 0));

                                schedules.add(schedule);
                            }
                            binding.timeTable.add(schedules);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Error", error.getMessage());
                        }
                    });
                }
            }
        });

        //initialize events in selected calendar
        horizontalCalendar.getCalendarListener().onDateSelected(selectedCalendar, 0);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}