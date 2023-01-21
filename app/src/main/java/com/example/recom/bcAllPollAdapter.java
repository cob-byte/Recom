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

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class bcAllPollAdapter extends RecyclerView.Adapter<bcAllPollAdapter.ViewHolder> {
    private DatabaseReference reference;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final FirebaseAuth firebaseProfile = FirebaseAuth.getInstance();
    private final FirebaseUser firebaseUser = firebaseProfile.getCurrentUser();
    private Context context;
    private ArrayList<bConsensus> allList;
    private ArrayList<String> pushKey;
    private long serverTimeOffset = 0;
    private static final String TAG = "Poll";

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public bcAllPollAdapter(Context context, ArrayList<bConsensus> allList, ArrayList<String> pushKey) {
        this.context = context;
        this.allList = allList;
        this.pushKey = pushKey;
    }

    @NonNull
    @Override
    public bcAllPollAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.brgy_all_poll, parent, false);
        return new bcAllPollAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull bcAllPollAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        bConsensus consensus = allList.get(position);

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

        holder.authorName.setText(consensus.getName());
        Picasso.get().load(consensus.getImage()).into(holder.profileImage);

        holder.viewPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewPoll = new Intent(context, brgyViewPoll.class);
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

    @Override
    public int getItemCount() {
        return allList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView authorName, pollTitle, pollDescription, pollDateTime, pollTotalVotes, pollTimeCount, pollTimeRemaining, viewPoll;
        public CircleImageView profileImage;

        public ViewHolder(@NonNull View itemView) {
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
        }
    }
}
