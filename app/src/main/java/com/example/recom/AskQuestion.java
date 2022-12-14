package com.example.recom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.recom.databinding.ActivityAskQuestionBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AskQuestion extends AppCompatActivity {
    private ActivityAskQuestionBinding binding;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private final FirebaseAuth firebaseProfile = FirebaseAuth.getInstance();
    private final FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAskQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        binding.TextOption3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 5){
                    binding.option4.setVisibility(View.VISIBLE);
                }
                else{
                    binding.option4.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
                                Toast.makeText(AskQuestion.this, "Your account is not verified yet.", Toast.LENGTH_LONG).show();
                            }
                            else if (binding.questionTitle.getText().toString().isEmpty()){
                                Toast.makeText(AskQuestion.this, "Please enter the title of your question.", Toast.LENGTH_LONG).show();
                                binding.questionTitle.setError("Title of the question is required.");
                                binding.questionTitle.requestFocus();
                            }
                            else if(binding.question.getText().toString().isEmpty()){
                                Toast.makeText(AskQuestion.this, "Please enter your question.", Toast.LENGTH_LONG).show();
                                binding.question.setError("Question is required.");
                                binding.question.requestFocus();
                            }
                            else if(binding.TextOption1.getText().toString().isEmpty() || binding.TextOption2.getText().toString().isEmpty()){
                                Toast.makeText(AskQuestion.this, "Kindly enter an option.", Toast.LENGTH_LONG).show();
                                binding.TextOption1.setError("This field is required.");
                                binding.TextOption1.requestFocus();
                                binding.TextOption2.setError("This field is required.");
                                binding.TextOption2.requestFocus();
                            } else{
                                new AlertDialog.Builder(AskQuestion.this)
                                        .setMessage("Are you sure you want to post this question?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if(firebaseUser == null){
                                                    Toast.makeText(AskQuestion.this, "Something went wrong! User details are not available at the moment.", Toast.LENGTH_LONG).show();
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

        //for option 3-4
        if(!binding.TextOption3.getText().toString().isEmpty()){
            if(!binding.TextOption4.getText().toString().isEmpty()){
                questionMap.put("answer3", binding.TextOption3.getText().toString());
                questionMap.put("answer4", binding.TextOption4.getText().toString());
            }
            else{
                questionMap.put("answer3", binding.TextOption3.getText().toString());
                questionMap.put("answer4", null);
            }
        }
        else{
            questionMap.put("answer3", null);
            questionMap.put("answer4", null);
        }

        //anon or not
        if(binding.AnonSwitch.isChecked()){
            questionMap.put("anon",  binding.AnonSwitch.isChecked());
            questionMap.put("name", null);
        } else {
            questionMap.put("anon",  binding.AnonSwitch.isChecked());
            questionMap.put("name", firebaseUser.getDisplayName());
            if(firebaseUser.getPhotoUrl() != null){
                questionMap.put("image", firebaseUser.getPhotoUrl().toString());

            }
        }

        databaseReference = database.getReference("communityConsensus");
        databaseReference.child("questions").push().setValue(questionMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AskQuestion.this, "Question posted.", Toast.LENGTH_LONG).show();
                    finish();
                }
                else{
                    Toast.makeText(AskQuestion.this, "Failed, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}