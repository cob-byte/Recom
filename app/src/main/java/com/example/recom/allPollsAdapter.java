package com.example.recom;

import static java.lang.Math.round;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class allPollsAdapter extends RecyclerView.Adapter<allPollsAdapter.MyViewHolder>{
    private DatabaseReference reference;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final FirebaseAuth firebaseProfile = FirebaseAuth.getInstance();
    private final FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();
    private Context context;
    private ArrayList<cConsensus> allList;
    private ArrayList<String> pushKey;
    private long serverTimeOffset = 0;
    private static final String TAG = "Poll";

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public allPollsAdapter(Context context, ArrayList<cConsensus> allList, ArrayList<String> pushKey) {
        this.context = context;
        this.allList = allList;
        this.pushKey = pushKey;
    }

    @NonNull
    @Override
    public allPollsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_poll, parent, false);
        return new allPollsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull allPollsAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        cConsensus consensus = allList.get(position);

        // Check if user has already upvoted
        if (consensus.upVoters != null && consensus.upVoters.containsKey(firebaseUser.getUid())) {
            holder.upvote.setColorFilter(ContextCompat.getColor(context, R.color.pink), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        // Check if user has already downvoted
        if (consensus.downVoters != null && consensus.downVoters.containsKey(firebaseUser.getUid())) {
            holder.downvote.setColorFilter(ContextCompat.getColor(context, R.color.pink), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        //get number of votes per post
        //initialize variables needed
        double answer1, answer2, answer3, answer4, total;

        //get answers count
        answer1 = consensus.getAnswer1Count();
        answer2 = consensus.getAnswer2Count();
        if(consensus.getAnswer3() != null) {
            if (consensus.getAnswer4() != null) {
                answer3 = consensus.getAnswer3Count();
                answer4 = consensus.getAnswer4Count();

                //get total if 4 answers
                total = answer1+answer2+answer3+answer4;
            }
            else{
                answer3 = consensus.getAnswer3Count();

                //get total if 3 answers
                total = answer1+answer2+answer3;
            }
        }else{
            //get total if 2 answers
            total = answer1+answer2;
        }
        holder.pollTotalVotes.setText("Total Votes: " + round(total));

        holder.pollTitle.setText(consensus.getTitle());

        holder.pollDescription.setText(consensus.getQuestion());
        String dateTime = consensus.getDate() + " " + consensus.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy HH:mm");
        try {
            long time = sdf.parse(dateTime).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            holder.pollDateTime.setText(ago);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(!consensus.getAnon()){
            holder.authorName.setText(consensus.getName());
            if(consensus.getImage() != null){
                Picasso.get().load(consensus.getImage()).into(holder.profileImage);
            }
        }

        if(String.valueOf(consensus.getVote()) == null){
            holder.pollCount.setText(0);
        }
        else{
            holder.pollCount.setText(String.valueOf(consensus.getVote()));
        }

        holder.upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upvotePoll(pushKey, position);
            }
        });

        holder.downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downvotePoll(pushKey, position);
            }
        });

        holder.viewPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewPoll = new Intent(context, Viewpoll.class);
                viewPoll.putExtra("pushKey", pushKey.get(position));
                viewPoll.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(viewPoll);
            }
        });

        //get time skew
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
                holder.pollTimeCount.setText(time);
            }
            public void onFinish() {
                holder.pollTimeRemaining.setVisibility(View.GONE);
                holder.pollTimeCount.setText("Poll is finished.");
            }
        }.start();
    }

    private void downvotePoll(ArrayList<String> pushKey, int position) {
        reference = database.getReference("communityConsensus").child("questions").child(pushKey.get(position));
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
                    consensus.vote = consensus.vote + 1;
                    consensus.downVoters.remove(firebaseUser.getUid());
                } else{
                    if(consensus.upVoters.containsKey(firebaseUser.getUid())){
                        // remove upvote from the poll
                        consensus.vote = consensus.vote - 1;
                        consensus.upVoters.remove(firebaseUser.getUid());
                    }

                    // downvote the poll
                    consensus.vote = consensus.vote - 1;
                    consensus.downVoters.put(firebaseUser.getUid(), true);
                }
                // Set value and report transaction success
                currentData.setValue(consensus);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if(!committed){
                    Toast.makeText(context, "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void upvotePoll(ArrayList<String> pushKey, int position) {
        reference = database.getReference("communityConsensus").child("questions").child(pushKey.get(position));
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
                    consensus.vote = consensus.vote - 1;
                    consensus.upVoters.remove(firebaseUser.getUid());
                } else{
                    if(consensus.downVoters.containsKey(firebaseUser.getUid())){
                        // remove downvote from the poll
                        consensus.vote = consensus.vote + 1;
                        consensus.downVoters.remove(firebaseUser.getUid());
                    }
                    // upvote the poll
                    consensus.vote = consensus.vote + 1;
                    consensus.upVoters.put(firebaseUser.getUid(), true);
                }
                // Set value and report transaction success
                currentData.setValue(consensus);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if(!committed){
                    Toast.makeText(context, "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return allList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView authorName, pollTitle, pollDescription, pollDateTime, pollTotalVotes, pollTimeCount, pollTimeRemaining, pollCount, viewPoll;
        public ImageButton upvote, downvote;
        public CircleImageView profileImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            viewPoll = itemView.findViewById(R.id.viewPoll);
            pollTitle = itemView.findViewById(R.id.pollTitle);
            pollDescription = itemView.findViewById(R.id.vPollQuestion);
            pollDateTime = itemView.findViewById(R.id.TimePosted);
            pollTotalVotes = itemView.findViewById(R.id.pollTotalVotes);
            profileImage = itemView.findViewById(R.id.profileImage);
            pollTimeCount = itemView.findViewById(R.id.pollTimeCount);
            pollTimeRemaining = itemView.findViewById(R.id.pollTimeRemaining);
            authorName = itemView.findViewById(R.id.vPollName);
            pollCount = itemView.findViewById(R.id.pollCountVote);
            upvote = itemView.findViewById(R.id.pollUpVote);
            downvote = itemView.findViewById(R.id.pollDownVote);
        }
    }
}
