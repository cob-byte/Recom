package com.example.recom;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.recom.databinding.ActivityAppointmentScheduleBinding;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class AppointmentSchedule extends AppCompatActivity {
    private ActivityAppointmentScheduleBinding binding;
    private int startHour = 0, startMinute = 0;
    private long timestampDate;
    private final List<String> selectedItems = new ArrayList<>();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference eventsRef = database.getReference("pendingSched");
    private final FirebaseAuth firebaseProfile = FirebaseAuth.getInstance();
    private final FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();

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
                        Calendar calendar = Calendar.getInstance();
                        TimeZone phTimeZone = TimeZone.getTimeZone("Asia/Manila");
                        calendar.setTimeZone(phTimeZone);
                        calendar.set(year, month, day);
                        timestampDate = calendar.getTimeInMillis();

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

        //hide the quantity input first
        binding.ChairsQuantity.setVisibility(View.GONE);
        binding.TablesQuantity.setVisibility(View.GONE);
        binding.TentsQuantity.setVisibility(View.GONE);

        //facilities
        binding.BtnEHall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle button click
                if (selectedItems.contains("ehall")) {
                    selectedItems.remove("ehall");
                    binding.BtnEHall.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AppointmentSchedule.this, R.color.pink)));
                } else {
                    selectedItems.add("ehall");
                    binding.BtnEHall.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AppointmentSchedule.this, R.color.temp)));
                }
            }
        });

        binding.BtnCourt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle button click
                if (selectedItems.contains("court")) {
                    selectedItems.remove("court");
                    binding.BtnCourt.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AppointmentSchedule.this, R.color.pink)));
                } else {
                    selectedItems.add("court");
                    binding.BtnCourt.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AppointmentSchedule.this, R.color.temp)));
                }
            }
        });

        binding.BtnChairs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle button click
                if (selectedItems.contains("chairs")) {
                    selectedItems.remove("chairs");
                    binding.BtnChairs.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AppointmentSchedule.this, R.color.pink)));
                    binding.ChairsQuantity.setVisibility(View.GONE);
                } else {
                    selectedItems.add("chairs");
                    binding.BtnChairs.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AppointmentSchedule.this, R.color.temp)));
                    binding.ChairsQuantity.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.BtnTables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle button click
                if (selectedItems.contains("tables")) {
                    selectedItems.remove("tables");
                    binding.BtnTables.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AppointmentSchedule.this, R.color.pink)));
                    binding.TablesQuantity.setVisibility(View.GONE);
                } else {
                    selectedItems.add("tables");
                    binding.BtnTables.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AppointmentSchedule.this, R.color.temp)));
                    binding.TablesQuantity.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.BtnTent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle button click
                if (selectedItems.contains("tents")) {
                    selectedItems.remove("tents");
                    binding.BtnTent.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AppointmentSchedule.this, R.color.pink)));
                    binding.TentsQuantity.setVisibility(View.GONE);
                } else {
                    selectedItems.add("tents");
                    binding.BtnTent.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AppointmentSchedule.this, R.color.temp)));
                    binding.TentsQuantity.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.BtnCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInputValid()){
                    new AlertDialog.Builder(AppointmentSchedule.this)
                    .setMessage("Are you sure you want to post this question?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //get pushkey
                            String eventId = eventsRef.push().getKey();

                            // Retrieve the quantity for each selected item
                            Map<String, Object> items = getEventItems(selectedItems);

                            int chairsD = (int) items.get("chairs");
                            int tablesD = (int) items.get("tables");
                            int tentsD = (int) items.get("tents");
                            boolean basketballCourtD = (boolean) items.get("court");
                            boolean eventHallD = (boolean) items.get("ehall");

                            Event event = new Event(binding.EventName.getText().toString(), binding.EventDate.getText().toString(), binding.editStartTime.getText().toString(),
                                    binding.editEndTime.getText().toString(), binding.editTextDescription.getText().toString(),
                                    chairsD, tablesD, tentsD, timestampDate, basketballCourtD, eventHallD, firebaseUser.getDisplayName());
                            eventsRef.child(eventId).setValue(event);

                            Toast.makeText(AppointmentSchedule.this, "Event Schedule has been sent, please wait for confirmation.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    })
                    .setNegativeButton("No",null)
                    .show();
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

    private boolean isInputValid() {
        boolean isValid = true;

        // Check if event name is not empty
        if (TextUtils.isEmpty(binding.EventName.getText().toString())) {
            // If event name is empty, set error message and set isValid to false
            binding.EventName.setError("Event name is required");
            isValid = false;
        }

        // Check if event description is not empty
        if (TextUtils.isEmpty(binding.editTextDescription.getText().toString())) {
            // If event name is empty, set error message and set isValid to false
            binding.editTextDescription.setError("Event description is required");
            isValid = false;
        }

        // Check if date is not empty and is from today onwards♦
        if (TextUtils.isEmpty(binding.EventDate.getText().toString())) {
            // If date is empty, set error message and set isValid to false
            binding.EventDate.setError("Event date is required");
            isValid = false;
        } else {
            // If date is not empty, check if it's from today onwards
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", new Locale("en", "PH"));
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            try {
                Date inputDate = sdf.parse(binding.EventDate.getText().toString());
                Calendar inputCalendar = Calendar.getInstance();
                inputCalendar.setTime(inputDate);

                if (inputCalendar.before(today)) {
                    binding.EventDate.setError("Date must be from today onwards");
                    isValid = false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                binding.EventDate.setError("Invalid date format");
                isValid = false;
            }
        }

        // Check if start time is not empty and is within 6 am to 8 pm
        if (TextUtils.isEmpty(binding.editStartTime.getText().toString())) {
            // If start time is empty, set error message and set isValid to false
            binding.editStartTime.setError("Event start time is required");
            isValid = false;
        } else {
            // If start time is not empty, check if it's within 6 am to 8 pm
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", new Locale("en", "PH"));
            try {
                Date inputTime = sdf.parse(binding.editStartTime.getText().toString());
                Calendar cal = Calendar.getInstance();
                cal.setTime(inputTime);
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                if (hour < 6 || hour >= 20) {
                    // If start time is before 6 am or after 8 pm, set error message and set isValid to false
                    binding.editStartTime.setError("Start time must be within 6 am to 8 pm");
                    isValid = false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                binding.editStartTime.setError("Invalid time format");
                isValid = false;
            }
        }

        // Check if end time is not empty and is within 6 am to 10 pm
        if (TextUtils.isEmpty(binding.editEndTime.getText().toString())) {
            // If end time is empty, set error message and set isValid to false
            binding.editEndTime.setError("End time is required");
            isValid = false;
        }
        else {
            // If start time is not empty, check if it's within 6 am to 8 pm
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", new Locale("en", "PH"));
            try {
                Date endTime = sdf.parse(binding.editEndTime.getText().toString());
                Calendar endTimeCalender = Calendar.getInstance();
                endTimeCalender.setTime(endTime);
                int hour = endTimeCalender.get(Calendar.HOUR_OF_DAY);
                if (hour < 6 || hour >= 22) {
                    binding.editEndTime.setError("End time must be within 6 am to 10 pm");
                    isValid = false;
                }
                if(!TextUtils.isEmpty(binding.editEndTime.getText().toString())){
                    Date startTimeDate = sdf.parse(binding.editStartTime.getText().toString());
                    Calendar startTimeCalendar = Calendar.getInstance();
                    startTimeCalendar.setTime(startTimeDate);
                    if(startTimeCalendar.getTimeInMillis() >= endTimeCalender.getTimeInMillis()){
                        binding.editEndTime.setError("End time must be more than start time.");
                        isValid = false;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                binding.editEndTime.setError("Invalid time format");
                isValid = false;
            }

            if (selectedItems.size() == 0) {
                binding.textView22.setError("Please select at least one Facility/Equipment.");
                return false;
            }
            else{
                for(String item : selectedItems){
                    if(item.equals("tents")){
                        if(TextUtils.isEmpty(binding.InputTentQty.getText().toString())){
                            binding.InputTentQty.setError("Field required");
                            return false;
                        }
                        else if(Integer.parseInt(binding.InputTentQty.getText().toString()) < 0){
                            binding.InputTentQty.setError("Quantity must be more than 0.");
                            return false;
                        }
                    }
                    if(item.equals("chairs")){
                        if(TextUtils.isEmpty(binding.InputCQty.getText().toString())){
                            binding.InputCQty.setError("Field required");
                            return false;
                        }
                        else if(Integer.parseInt(binding.InputCQty.getText().toString()) < 0){
                            binding.InputTentQty.setError("Quantity must be more than 0.");
                            return false;
                        }
                    }
                    if(item.equals("tables")){
                        if(TextUtils.isEmpty(binding.InputTQty.getText().toString())){
                            binding.InputTQty.setError("Field required");
                            return false;
                        }
                        else if(Integer.parseInt(binding.InputTQty.getText().toString()) < 0){
                            binding.InputTentQty.setError("Quantity must be more than 0.");
                            return false;
                        }
                    }
                }
            }

        }
        return isValid;
    }

    private Map<String, Object> getEventItems(List<String> selectedItems) {
        Map<String, Object> items = new HashMap<>();
        if (selectedItems.contains("ehall")) {
            items.put("ehall", true);
        } else {
            items.put("ehall", false);
        }
        if (selectedItems.contains("court")) {
            items.put("court", true);
        } else {
            items.put("court", false);
        }
        if (selectedItems.contains("chairs")) {
            int chairsQuantity = Integer.parseInt(binding.InputCQty.getText().toString());
            items.put("chairs", chairsQuantity);
        } else {
            items.put("chairs", 0);
        }
        if (selectedItems.contains("tables")) {
            int tablesQuantity = Integer.parseInt(binding.InputTQty.getText().toString());
            items.put("tables", tablesQuantity);
        } else {
            items.put("tables", 0);
        }
        if (selectedItems.contains("tents")) {
            int tentsQuantity = Integer.parseInt(binding.InputTentQty.getText().toString());
            items.put("tents", tentsQuantity);
        } else {
            items.put("tents", 0);
        }
        return items;
    }


}