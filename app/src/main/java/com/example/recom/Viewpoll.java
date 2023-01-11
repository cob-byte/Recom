package com.example.recom;

import static java.lang.Math.round;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.recom.databinding.ActivityViewpollBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Viewpoll extends AppCompatActivity {
    private ActivityViewpollBinding binding;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final FirebaseAuth firebaseProfile = FirebaseAuth.getInstance();
    private final FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();
    private DatabaseReference reference;
    private String pushKey;
    private static final String TAG = "Viewpoll";

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseUser == null){
            Toast.makeText(Viewpoll.this, "Something went wrong! di ka naka login beh", Toast.LENGTH_LONG).show();
        }
        else{
            Intent intent = getIntent();
            pushKey = intent.getStringExtra("pushKey");
            //check if the user already voted
            reference = database.getReference("communityConsensus");
            showPoll(pushKey);
        }
    }

    private void showPoll(String pushKey) {
        reference = database.getReference("communityConsensus").child("questions").child(pushKey);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cConsensus consensus = snapshot.getValue(cConsensus.class);
                if(consensus != null){
                    boolean anon = consensus.getAnon();
                    binding.vPollTitle.setText(consensus.getTitle());
                    if(anon == false){
                        binding.vPollName.setText(consensus.getName());
                    }
                    else{
                        binding.vPollName.setText("Anonymous");
                    }
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
                    binding.vPollQuestion.setText(consensus.getQuestion());

                    checkVote(consensus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "postTransaction:onComplete:" + error);
            }
        });
    }

    private void checkVote(cConsensus consensus) {
        if(consensus.answer1Vote.containsKey(firebaseUser.getUid()) || consensus.answer2Vote.containsKey(firebaseUser.getUid())
                || consensus.answer3Vote.containsKey(firebaseUser.getUid()) || consensus.answer4Vote.containsKey(firebaseUser.getUid())){
            binding.pollOptions.setVisibility(View.GONE);
            binding.BtnVote.setVisibility(View.GONE);
            binding.alreadyVoted.setVisibility(View.VISIBLE);
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
        binding = ActivityViewpollBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        binding.pollOptions.clearCheck();

        binding.BtnVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int answer;
                int selectedAnswerID = binding.pollOptions.getCheckedRadioButtonId();
                switch(selectedAnswerID){
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
                        Toast.makeText(Viewpoll.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

        binding.Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void downvotePoll(String pushKey) {
        reference = database.getReference("communityConsensus").child("questions").child(pushKey);
        reference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                cConsensus consensus = currentData.getValue(cConsensus.class);
                if(consensus == null){
                    return Transaction.success(currentData);
                }

                if(consensus.downVoters.containsKey(firebaseUser.getUid())){
                    // remove downvote from the poll
                    consensus.downvote = consensus.downvote - 1;
                    consensus.downVoters.remove(firebaseUser.getUid());
                } else{
                    // downvote the poll
                    consensus.downvote = consensus.downvote + 1;
                    consensus.downVoters.remove(firebaseUser.getUid());
                }
                // Set value and report transaction success
                currentData.setValue(consensus);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                Log.d(TAG, "postTransaction:onComplete:" + error);
            }
        });
    }

    private void upvotePoll(String pushKey) {
        reference = database.getReference("communityConsensus").child("questions").child(pushKey);
        reference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                cConsensus consensus = currentData.getValue(cConsensus.class);
                if(consensus == null){
                    return Transaction.success(currentData);
                }

                if(consensus.upVoters.containsKey(firebaseUser.getUid())){
                    // remove upvote from the poll
                    consensus.upvote = consensus.upvote - 1;
                    consensus.upVoters.remove(firebaseUser.getUid());
                } else{
                    // upvote the poll
                    consensus.upvote = consensus.upvote + 1;
                    consensus.upVoters.remove(firebaseUser.getUid());
                }
                // Set value and report transaction success
                currentData.setValue(consensus);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                Log.d(TAG, "postTransaction:onComplete:" + error);
            }
        });
    }

    private void castVote(int answer, String pushKey) {
        reference = database.getReference("communityConsensus").child("questions").child(pushKey);
        reference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                cConsensus consensus = currentData.getValue(cConsensus.class);
                if(consensus == null){
                    return Transaction.success(currentData);
                }
                switch(answer){
                    case 1:
                        if(consensus.answer1Vote.containsKey(firebaseUser.getUid()) || consensus.answer2Vote.containsKey(firebaseUser.getUid())
                                || consensus.answer3Vote.containsKey(firebaseUser.getUid()) || consensus.answer4Vote.containsKey(firebaseUser.getUid())){
                            Toast.makeText(Viewpoll.this, "The user has already voted.", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(Viewpoll.this, "The user has already voted.", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(Viewpoll.this, "The user has already voted.", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(Viewpoll.this, "The user has already voted.", Toast.LENGTH_LONG).show();
                        }
                        else{
                            // vote answer 4 from the poll
                            consensus.answer4Count += 1;
                            consensus.answer4Vote.put(firebaseUser.getUid(), true);
                        }
                        break;
                    default:
                        Toast.makeText(Viewpoll.this, "Something went wrong! Please try again.", Toast.LENGTH_LONG).show();
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
                        cConsensus consensus = snapshot.getValue(cConsensus.class);
                        checkVote(consensus);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}