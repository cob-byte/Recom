package com.example.recom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.recom.databinding.ActivityAppointmentScheduleBinding;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AppointmentSchedule extends AppCompatActivity {
    private ActivityAppointmentScheduleBinding binding;
    private int startHour = 0, startMinute = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        binding.EventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //initialize calendar/year,month,day
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                //set up date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(AppointmentSchedule.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // Convert the selected date to a string/PH time
                        String selectedDate = String.format(new Locale("en", "PH"),"%04d-%02d-%02d", year, month + 1, day);
                        // Set the selected date in the TextView
                        binding.EventDate.setText(selectedDate);
                    }
                }, year, month, day);

                //set constraint
                Calendar c = Calendar.getInstance();
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        binding.editStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set up time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AppointmentSchedule.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        //check if the time is between 6 am and 8 pm
                        // to give an allowance because the max rent of basketball court
                        // in my case is 10pm(2hours)
                        if (hour >= 6 && hour <= 20) {
                            //format the time to PH time zone in hh:mm format
                            String selectedTime = String.format(new Locale("en", "PH"), "%02d:%02d", hour, minute);
                            binding.editStartTime.setText(selectedTime);
                        } else {
                            Toast.makeText(AppointmentSchedule.this, "The start time must be between 6:00 AM and 8:00 PM", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 6, 0, false);
                //show the time picker
                timePickerDialog.show();
            }
        });

        //end time
        binding.editEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the start time
                String startTimeString = binding.editStartTime.getText().toString();
                //check is start time is empty to show the time picker
                if (!startTimeString.isEmpty()) {
                    String[] startTimeArray = startTimeString.split(":");
                    startHour = Integer.parseInt(startTimeArray[0]);
                    startMinute = Integer.parseInt(startTimeArray[1]);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(AppointmentSchedule.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                            //check if the selected time is more than the start time earlier
                            if (hour >= startHour && minute >= startMinute && hour >= 6 && hour <= 22) {
                                String selectedTime = String.format(Locale.ENGLISH, "%02d:%02d", hour, minute);
                                binding.editEndTime.setText(selectedTime);
                            } else {
                                Toast.makeText(AppointmentSchedule.this, "End time must be greater than start time and until 10:00PM", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, startHour, startMinute, false);
                    timePickerDialog.show();
                }
                else{
                    Toast.makeText(AppointmentSchedule.this, "Please enter a start time.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}