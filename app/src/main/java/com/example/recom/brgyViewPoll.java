package com.example.recom;

import static java.lang.Math.round;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.recom.databinding.ActivityBrgyViewPollBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class brgyViewPoll extends AppCompatActivity {
    private ActivityBrgyViewPollBinding binding;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final FirebaseAuth firebaseProfile = FirebaseAuth.getInstance();
    private final FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();
    private DatabaseReference reference;
    private pollCommentAdapter myAdapter;
    private RecyclerView comPoll;
    private ArrayList<pollComment> comment;
    private String pushKey;
    private ValueEventListener listener;
    private long serverTimeOffset = 0, timeLeft;
    private static final String TAG = "Barangay Viewpoll";

    @Override
    protected void onStop() {
        super.onStop();
        if (reference != null && listener != null) {
            reference.removeEventListener(listener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseUser == null){
            Toast.makeText(brgyViewPoll.this, "Something went wrong! di ka naka login beh", Toast.LENGTH_LONG).show();
        }
        else{
            Intent intent = getIntent();
            pushKey = intent.getStringExtra("pushKey");
            //check if the user already voted
            reference = database.getReference("barangayConsensus");
            showPoll(pushKey);
            showComments();
        }
    }

    private void showPoll(String pushKey) {
        //get clock skew
        DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                serverTimeOffset = (long) snapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Listener was cancelled");
            }
        });

        reference = database.getReference("barangayConsensus").child("questions").child(pushKey);
        listener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bConsensus consensus = snapshot.getValue(bConsensus.class);
                if(consensus != null){
                    //poll title setter
                    binding.vPollTitle.setText(consensus.getTitle());
                    //name and brgy pic
                    binding.vPollName.setText(consensus.getName());
                    Picasso.get().load(consensus.getImage()).into(binding.profileImage);

                    //date time set
                    String dateTime = consensus.getDate() + " " + consensus.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy HH:mm");
                    try {
                        long time = sdf.parse(dateTime).getTime();
                        long now = System.currentTimeMillis();
                        CharSequence ago =
                                DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
                        binding.TimePosted.setText(ago);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    //poll question
                    binding.vPollQuestion.setText(consensus.getQuestion());

                    long seconds = consensus.getSeconds();
                    long start = consensus.getStart();
                    final long timeLeft = (long) ((seconds * 1000) - (System.currentTimeMillis() - start - serverTimeOffset));
                    new CountDownTimer(timeLeft, 100) {
                        public void onTick(long millisUntilFinished) {
                            long seconds = millisUntilFinished / 1000;
                            long minutes = seconds / 60;
                            long hours = minutes / 60;
                            long days = hours / 24;
                            String time = days+ " days" +": " +hours % 24 + ":" + minutes % 60 + ":" + seconds % 60;
                            binding.TimeCount.setText(time);
                        }
                        @SuppressLint("ClickableViewAccessibility")
                        public void onFinish() {
                            binding.pollOptions.setVisibility(View.GONE);
                            binding.BtnVote.setEnabled(false);
                            binding.BtnVote.setText("Poll is Finished");
                            binding.BtnVote.setBackgroundColor(getResources().getColor(R.color.gray));
                            binding.BtnVote.setTextColor(getResources().getColor(R.color.white));

                            //show results
                            binding.alreadyVoted.setVisibility(View.VISIBLE);

                            //disable manual touch of users
                            binding.seekBar1.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    return true;
                                }
                            });

                            binding.seekBar2.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    return true;
                                }
                            });

                            binding.seekBar3.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    return true;
                                }
                            });

                            binding.seekBar4.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    return true;
                                }
                            });


                            binding.TimeRemaining.setVisibility(View.GONE);
                            binding.TimeCount.setText("Poll is finished.");
                        }
                    }.start();

                    checkVote(consensus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(brgyViewPoll.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n", "DefaultLocale"})
    private void checkVote(bConsensus consensus) {
        if(consensus.answer1Vote.containsKey(firebaseUser.getUid()) || consensus.answer2Vote.containsKey(firebaseUser.getUid())
                || consensus.answer3Vote.containsKey(firebaseUser.getUid()) || consensus.answer4Vote.containsKey(firebaseUser.getUid())){
            //hide the options
            binding.pollOptions.setVisibility(View.GONE);
            binding.BtnVote.setEnabled(false);
            binding.BtnVote.setText("Already Voted");
            binding.BtnVote.setBackgroundColor(getResources().getColor(R.color.gray));
            binding.BtnVote.setTextColor(getResources().getColor(R.color.white));

            //show the results
            binding.alreadyVoted.setVisibility(View.VISIBLE);

            //disable manual touch of users
            binding.seekBar1.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });

            binding.seekBar2.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });

            binding.seekBar3.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });

            binding.seekBar4.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });

            //initialize variables needed
            double answer1, answer2, answer3, answer4,
                    percent1, percent2, percent3, percent4, total;

            //get answers count
            answer1 = consensus.getAnswer1Count();
            answer2 = consensus.getAnswer2Count();
            if(consensus.getAnswer3() != null) {
                if (consensus.getAnswer4() != null) {
                    binding.pollResult3.setVisibility(View.VISIBLE);
                    binding.pollResult4.setVisibility(View.VISIBLE);

                    answer3 = consensus.getAnswer3Count();
                    answer4 = consensus.getAnswer4Count();

                    //get total if 4 answers
                    total = answer1+answer2+answer3+answer4;

                    //get percent if 4 answers
                    percent1=(answer1/total)*100;
                    percent2=(answer2/total)*100;
                    percent3=(answer3/total)*100;
                    percent4=(answer4/total)*100;

                    // set percent on text view
                    binding.progressBar1.setText(String.format("%.0f%%",percent1));
                    // Set progress on seekbar
                    binding.seekBar1.setProgress((int)percent1);

                    binding.progressBar2.setText(String.format("%.0f%%",percent2));
                    binding.seekBar2.setProgress((int)percent2);
                    binding.progressBar3.setText(String.format("%.0f%%",percent3));
                    binding.seekBar3.setProgress((int)percent3);
                    binding.progressBar4.setText(String.format("%.0f%%",percent4));
                    binding.seekBar4.setProgress((int)percent4);
                }
                else{
                    binding.pollResult3.setVisibility(View.VISIBLE);
                    answer3 = consensus.getAnswer3Count();

                    //get total if 3 answers
                    total = answer1+answer2+answer3;

                    //get percent if 3 answers
                    percent1=(answer1/total)*100;
                    percent2=(answer2/total)*100;
                    percent3=(answer3/total)*100;

                    // set percent on text view
                    binding.progressBar1.setText(String.format("%.0f%%",percent1));
                    // Set progress on seekbar
                    binding.seekBar1.setProgress((int)percent1);

                    binding.progressBar2.setText(String.format("%.0f%%",percent2));
                    binding.seekBar2.setProgress((int)percent2);
                    binding.progressBar3.setText(String.format("%.0f%%",percent3));
                    binding.seekBar3.setProgress((int)percent3);
                }
            }else{
                //get total if 2 answers
                total = answer1+answer2;

                //get percent if 2 answers
                percent1=(answer1/total)*100;
                percent2=(answer2/total)*100;

                // set percent on text view
                binding.progressBar1.setText(String.format("%.0f%%",percent1));
                // Set progress on seekbar
                binding.seekBar1.setProgress((int)percent1);

                binding.progressBar2.setText(String.format("%.0f%%",percent2));
                binding.seekBar2.setProgress((int)percent2);
            }
            binding.TotalVotes.setText("Total Votes: " + round(total));
        }
        else{
            binding.radioButton1.setText(consensus.getAnswer1());
            binding.radioButton2.setText(consensus.getAnswer2());
            if(consensus.getAnswer3() != null){
                if(consensus.getAnswer4() != null){
                    binding.radioButton3.setVisibility(View.VISIBLE);
                    binding.radioButton4.setVisibility(View.VISIBLE);
                    binding.radioButton3.setText(consensus.getAnswer3());
                    binding.radioButton4.setText(consensus.getAnswer4());
                }
                else{
                    binding.radioButton3.setVisibility(View.VISIBLE);
                    binding.radioButton3.setText(consensus.getAnswer3());
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBrgyViewPollBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        binding.pollOptions.clearCheck();

        Intent intent = getIntent();
        pushKey = intent.getStringExtra("pushKey");

        reference = database.getReference("barangayConsensus").child("questions").child(pushKey);
        listener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bConsensus consensus = snapshot.getValue(bConsensus.class);

                long seconds = consensus.getSeconds();
                long start = consensus.getStart();

                timeLeft = (long) ((seconds * 1000) - (System.currentTimeMillis() - start - serverTimeOffset));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.BtnVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference = database.getReference("Users");
                reference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        int answer;
                        int selectedAnswerID = binding.pollOptions.getCheckedRadioButtonId();
                        if (user != null) {
                            if (user.userRole == 0) {
                                Toast.makeText(brgyViewPoll.this, "Your account is not verified yet.", Toast.LENGTH_LONG).show();
                            } else if (timeLeft <= 0) {
                                Toast.makeText(brgyViewPoll.this, "Poll is Finished!", Toast.LENGTH_LONG).show();
                            } else {
                                switch (selectedAnswerID) {
                                    case R.id.radioButton1:
                                        answer = 1;
                                        castVote(answer, pushKey);
                                        break;
                                    case R.id.radioButton2:
                                        answer = 2;
                                        castVote(answer, pushKey);
                                        break;
                                    case R.id.radioButton3:
                                        answer = 3;
                                        castVote(answer, pushKey);
                                        break;
                                    case R.id.radioButton4:
                                        answer = 4;
                                        castVote(answer, pushKey);
                                        break;
                                    default:
                                        Toast.makeText(brgyViewPoll.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
                                        break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(brgyViewPoll.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        binding.btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.pollComments.getVisibility() == View.VISIBLE){
                    binding.pollComments.setVisibility(View.GONE);
                }
                else{
                    binding.pollComments.setVisibility(View.VISIBLE);
                    showComments();
                }
            }
        });

        binding.addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference = database.getReference("Users");
                reference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if(user != null){
                            if(user.userRole == 0){
                                Toast.makeText(brgyViewPoll.this, "Your account is not verified yet.", Toast.LENGTH_LONG).show();
                            }
                            else if(binding.textComment.getText().toString().isEmpty()){
                                Toast.makeText(brgyViewPoll.this, "Please enter the contents of your comment.", Toast.LENGTH_LONG).show();
                                binding.textComment.setError("Fill in.");
                                binding.textComment.requestFocus();
                            }
                            else{
                                addNewComment();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(brgyViewPoll.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        binding.btnAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ask = new Intent(brgyViewPoll.this, AskQuestion.class);
                ask.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ask);
            }
        });

        binding.btnPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent seeMyPoll = new Intent(brgyViewPoll.this, MyPoll_cc.class);
                seeMyPoll.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(seeMyPoll);
            }
        });

        binding.Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addNewComment() {
        //initialize hashmap
        HashMap<String, Object> commentMap = new HashMap<>();

        //get date and time
        Calendar calForDateTime = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yy");
        final String saveCurrentDate = currentDate.format(calForDateTime.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        final String  saveCurrentTime = currentTime.format(calForDateTime.getTime());

        commentMap.put("date", saveCurrentDate);
        commentMap.put("time", saveCurrentTime);
        commentMap.put("name", firebaseUser.getDisplayName());
        commentMap.put("comment", binding.textComment.getText().toString());


        if(firebaseUser.getPhotoUrl() != null){
            commentMap.put("image", firebaseUser.getPhotoUrl().toString());
        }

        reference = database.getReference("barangayConsensus").child("comments").child(pushKey);
        reference.push().setValue(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    binding.textComment.setText(null);
                    showComments();
                }
                else{
                    Toast.makeText(brgyViewPoll.this, "Something went wrong! Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showComments() {
        reference = database.getReference("barangayConsensus").child("comments").child(pushKey);
        comPoll = binding.comment;
        comPoll.setHasFixedSize(true);
        comPoll.setLayoutManager(new LinearLayoutManager(this));
        comment = new ArrayList<pollComment>();
        myAdapter = new pollCommentAdapter(this, comment);

        listener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null){
                    comment.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        comPoll.setAdapter(myAdapter);
                        pollComment pollcomment = dataSnapshot.getValue(pollComment.class);
                        comment.add(pollcomment);
                        int commentNumber = (int) snapshot.getChildrenCount();
                        if(commentNumber < 2){
                            binding.btnComments.setText(commentNumber + " comment");
                        }
                        else{
                            binding.btnComments.setText(commentNumber + " comments");
                        }
                    }
                    myAdapter.notifyDataSetChanged();
                }
                else{
                    binding.btnComments.setText("0 comment");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(brgyViewPoll.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void castVote(int answer, String pushKey) {
        reference = database.getReference("barangayConsensus").child("questions").child(pushKey);
        reference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                bConsensus consensus = currentData.getValue(bConsensus.class);
                if(consensus == null){
                    return Transaction.success(currentData);
                }
                switch(answer){
                    case 1:
                        if(consensus.answer1Vote.containsKey(firebaseUser.getUid()) || consensus.answer2Vote.containsKey(firebaseUser.getUid())
                                || consensus.answer3Vote.containsKey(firebaseUser.getUid()) || consensus.answer4Vote.containsKey(firebaseUser.getUid())){
                            Toast.makeText(brgyViewPoll.this, "The user has already voted.", Toast.LENGTH_LONG).show();
                        }
                        else{
                            // vote answer 1 from the poll
                            consensus.answer1Count += 1;
                            consensus.answer1Vote.put(firebaseUser.getUid(), true);
                        }
                        break;
                    case 2:
                        if(consensus.answer1Vote.containsKey(firebaseUser.getUid()) || consensus.answer2Vote.containsKey(firebaseUser.getUid())
                                || consensus.answer3Vote.containsKey(firebaseUser.getUid()) || consensus.answer4Vote.containsKey(firebaseUser.getUid())){
                            Toast.makeText(brgyViewPoll.this, "The user has already voted.", Toast.LENGTH_LONG).show();
                        }
                        else{
                            // vote answer 2 from the poll
                            consensus.answer2Count += 1;
                            consensus.answer2Vote.put(firebaseUser.getUid(), true);
                        }
                        break;
                    case 3:
                        if(consensus.answer1Vote.containsKey(firebaseUser.getUid()) || consensus.answer2Vote.containsKey(firebaseUser.getUid())
                                || consensus.answer3Vote.containsKey(firebaseUser.getUid()) || consensus.answer4Vote.containsKey(firebaseUser.getUid())){
                            Toast.makeText(brgyViewPoll.this, "The user has already voted.", Toast.LENGTH_LONG).show();
                        }
                        else{
                            // vote answer 3 from the poll
                            consensus.answer3Count += 1;
                            consensus.answer3Vote.put(firebaseUser.getUid(), true);
                        }
                        break;
                    case 4:
                        if(consensus.answer1Vote.containsKey(firebaseUser.getUid()) || consensus.answer2Vote.containsKey(firebaseUser.getUid())
                                || consensus.answer3Vote.containsKey(firebaseUser.getUid()) || consensus.answer4Vote.containsKey(firebaseUser.getUid())){
                            Toast.makeText(brgyViewPoll.this, "The user has already voted.", Toast.LENGTH_LONG).show();
                        }
                        else{
                            // vote answer 4 from the poll
                            consensus.answer4Count += 1;
                            consensus.answer4Vote.put(firebaseUser.getUid(), true);
                        }
                        break;
                    default:
                        Toast.makeText(brgyViewPoll.this, "Something went wrong! Please try again.", Toast.LENGTH_LONG).show();
                        break;
                }
                // Set value and report transaction success
                currentData.setValue(consensus);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        bConsensus consensus = snapshot.getValue(bConsensus.class);
                        checkVote(consensus);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if(!committed) {
                            Toast.makeText(brgyViewPoll.this, "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}