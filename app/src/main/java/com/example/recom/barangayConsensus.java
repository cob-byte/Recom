package com.example.recom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.recom.databinding.ActivityBarangayConsensusBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class barangayConsensus extends AppCompatActivity {
    private ActivityBarangayConsensusBinding binding;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private String[] day, hour, minute;
    private ArrayAdapter<String> adapterDay, adapterHour, adapterMinute;
    private final FirebaseAuth firebaseProfile = FirebaseAuth.getInstance();
    private final FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBarangayConsensusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        binding.TextOption3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 0){
                    binding.option4.setVisibility(View.VISIBLE);
                }
                else if(charSequence.toString().replace(" ", "").length() == 0){
                    binding.option4.setVisibility(View.GONE);
                }
                else{

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //initialize arrays
        day = new String[8];
        hour = new String[24];
        minute = new String[60];

        //get data for arrays
        for(int i = 0; i < 60; i++){
            if(i > 7){
                if(i>23){
                    minute[i] = String.valueOf(i);
                }
                else {
                    hour[i] = String.valueOf(i);
                    minute[i] = String.valueOf(i);
                }
            }
            else{
                day[i] = String.valueOf(i);
                hour[i] = String.valueOf(i);
                minute[i] = String.valueOf(i);
            }
        }

        //initialize adapter
        adapterDay = new ArrayAdapter<String>(barangayConsensus.this, R.layout.list_item, day);
        adapterHour = new ArrayAdapter<String>(barangayConsensus.this, R.layout.list_item, hour);
        adapterMinute = new ArrayAdapter<String>(barangayConsensus.this, R.layout.list_item, minute);

        //set adapter
        binding.dayPicker.setAdapter(adapterDay);
        binding.hourPicker.setAdapter(adapterHour);
        binding.minutePicker.setAdapter(adapterMinute);

        //set default choice for dropdown
        binding.dayPicker.setText("1", false);
        binding.hourPicker.setText("0", false);
        binding.minutePicker.setText("0", false);

        //listener
        binding.dayPicker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                if(item.equals("7")){
                    binding.hourPicker.setEnabled(false);
                    binding.minutePicker.setEnabled(false);
                }
                else if(item.equals("0")){
                    binding.hourPicker.setText("1", false);
                }
                else{
                    binding.hourPicker.setEnabled(true);
                    binding.minutePicker.setEnabled(true);
                }
            }
        });

        binding.hourPicker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                if(item.equals("0")){
                    binding.minutePicker.setText("5", false);
                }
            }
        });


        binding.minutePicker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                if(Integer.parseInt(item) < 5){
                    binding.minutePicker.setText("5", false);
                    Toast.makeText(barangayConsensus.this, "Minimum poll duration is 5 minutes.", Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference = database.getReference("Users");
                databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if(user != null){
                            if(user.userRole == 0){
                                Toast.makeText(barangayConsensus.this, "Your account is not verified yet.", Toast.LENGTH_LONG).show();
                            }
                            else if(user.userRole == 1){
                                Toast.makeText(barangayConsensus.this, "Something went wrong! Please try again.", Toast.LENGTH_LONG).show();
                            }
                            else if (binding.questionTitle.getText().toString().isEmpty()){
                                Toast.makeText(barangayConsensus.this, "Please enter the title of your question.", Toast.LENGTH_LONG).show();
                                binding.questionTitle.setError("Title of the question is required.");
                                binding.questionTitle.requestFocus();
                            }
                            else if ((binding.questionTitle.getText().toString().length() < 20) || (binding.questionTitle.getText().toString().length() > 70)){
                                Toast.makeText(barangayConsensus.this, "Length of the title must be between 20 to 70 characters.", Toast.LENGTH_LONG).show();
                                binding.questionTitle.setError("Length requirements do not meet.");
                                binding.questionTitle.requestFocus();
                            }
                            else if(binding.question.getText().toString().isEmpty()){
                                Toast.makeText(barangayConsensus.this, "Please enter your question.", Toast.LENGTH_LONG).show();
                                binding.question.setError("Question is required.");
                                binding.question.requestFocus();
                            }
                            else if ((binding.question.getText().toString().length() < 50) || (binding.question.getText().toString().length() > 160)){
                                Toast.makeText(barangayConsensus.this, "Length of the question must be between 50 to 160 characters.", Toast.LENGTH_LONG).show();
                                binding.questionTitle.setError("Length requirements do not meet.");
                                binding.questionTitle.requestFocus();
                            }
                            else if(binding.TextOption1.getText().toString().isEmpty() || binding.TextOption1.toString().replace(" ", "").length() == 0
                                    || binding.TextOption2.getText().toString().isEmpty() || binding.TextOption2.toString().replace(" ", "").length() == 0){
                                Toast.makeText(barangayConsensus.this, "Kindly enter an option.", Toast.LENGTH_LONG).show();
                                binding.TextOption1.setError("This field is required.");
                                binding.TextOption1.requestFocus();
                                binding.TextOption2.setError("This field is required.");
                                binding.TextOption2.requestFocus();
                            }
                            else if(binding.TextOption1.getText().toString().length() > 25 || binding.TextOption2.getText().toString().length() > 25){
                                Toast.makeText(barangayConsensus.this, "Length of the answer is too long.", Toast.LENGTH_LONG).show();
                                binding.questionTitle.setError("Answer is too long.");
                                binding.questionTitle.requestFocus();
                            }
                            else{
                                new AlertDialog.Builder(barangayConsensus.this)
                                        .setMessage("Are you sure you want to post this question?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if(firebaseUser == null){
                                                    Toast.makeText(barangayConsensus.this, "Something went wrong! User details are not available at the moment.", Toast.LENGTH_LONG).show();
                                                }
                                                else{
                                                    addNewQuestion(snapshot);
                                                }
                                            }
                                        })
                                        .setNegativeButton("No",null)
                                        .show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(barangayConsensus.this, "Failed, please try again.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        binding.Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addNewQuestion(DataSnapshot snapshot) {
        //get date and time
        Calendar calForDateTime = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yy");
        final String saveCurrentDate = currentDate.format(calForDateTime.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        final String  saveCurrentTime = currentTime.format(calForDateTime.getTime());


        //initialize hashmap
        HashMap<String, Object> questionMap = new HashMap<>();

        //get data
        questionMap.put("author", firebaseUser.getUid());
        questionMap.put("title", binding.questionTitle.getText().toString());
        questionMap.put("question", binding.question.getText().toString());
        questionMap.put("answer1", binding.TextOption1.getText().toString());
        questionMap.put("answer2", binding.TextOption2.getText().toString());

        //date
        questionMap.put("date", saveCurrentDate);
        questionMap.put("time", saveCurrentTime);

        //timer
        long finalDay, finalHour, finalMinute, totalSeconds;

        finalDay = Long.parseLong(binding.dayPicker.getText().toString()) * 86400L;
        finalHour = Long.parseLong(binding.hourPicker.getText().toString()) * 3600L;
        finalMinute = Long.parseLong(binding.minutePicker.getText().toString()) * 60L;
        totalSeconds = finalDay + finalHour + finalMinute;
        questionMap.put("start", ServerValue.TIMESTAMP);
        questionMap.put("seconds", totalSeconds);


        //for option 3-4
        if(!binding.TextOption3.getText().toString().isEmpty()){
            if(!binding.TextOption4.getText().toString().isEmpty()){
                if(!(binding.TextOption3.toString().replace(" ", "").length() == 0)) {
                    if (!(binding.TextOption4.toString().replace(" ", "").length() == 0)) {
                        questionMap.put("answer3", binding.TextOption3.getText().toString());
                        questionMap.put("answer4", binding.TextOption4.getText().toString());
                    } else {
                        questionMap.put("answer3", binding.TextOption3.getText().toString());
                        questionMap.put("answer4", null);
                    }
                }
            }
            else{
                if(!(binding.TextOption3.toString().replace(" ", "").length() == 0)) {
                    questionMap.put("answer3", binding.TextOption3.getText().toString());
                    questionMap.put("answer4", null);
                }
                else{
                    questionMap.put("answer3", null);
                    questionMap.put("answer4", null);
                }
            }
        }
        else{
            questionMap.put("answer3", null);
            questionMap.put("answer4", null);
        }

        //name and default image
        questionMap.put("name", firebaseUser.getDisplayName());
        questionMap.put("image", "https://firebasestorage.googleapis.com/v0/b/recom-e0d64.appspot.com/o/Barangay.svg.png?alt=media&token=4919230c-83ba-45b9-a2f5-3557c29395d1");


        databaseReference = database.getReference("barangayConsensus");
        databaseReference.child("questions").push().setValue(questionMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(barangayConsensus.this, "Question posted.", Toast.LENGTH_LONG).show();
                    finish();
                }
                else{
                    Toast.makeText(barangayConsensus.this, "Failed, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}