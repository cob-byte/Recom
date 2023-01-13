package com.example.recom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private ValueEventListener listener;
    private Context context;
    private ArrayList<cConsensus> allList;
    private ArrayList<String> pushKey;
    private static final String TAG = "Poll";

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

        //get number of comments per post
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference;
        reference = database.getReference("communityConsensus").child("questions").child(pushKey.get(position)).child("comments");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int commentNumber = (int) snapshot.getChildrenCount();
                if(commentNumber < 2){
                    holder.comment.setText(commentNumber + " comment");
                }
                else{
                    holder.comment.setText(commentNumber + " comments");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Something went wrong. Unable to fetch the number of comments.", Toast.LENGTH_LONG).show();
            }
        });

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
                Log.d(TAG, "postTransaction:onComplete:" + error);
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
                Log.d(TAG, "postTransaction:onComplete:" + error);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public RelativeLayout relativeLayout;
        public TextView authorName, pollTitle, pollDescription, pollDateTime, comment, pollTimeCount, pollCount;
        public ImageButton upvote, downvote;
        public CircleImageView profileImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.PollLayout);
            pollTitle = itemView.findViewById(R.id.pollTitle);
            pollDescription = itemView.findViewById(R.id.vPollQuestion);
            pollDateTime = itemView.findViewById(R.id.TimePosted);
            comment = itemView.findViewById(R.id.pollComment);
            profileImage = itemView.findViewById(R.id.profileImage);
            pollTimeCount = itemView.findViewById(R.id.pollTimeCount);
            authorName = itemView.findViewById(R.id.vPollName);
            pollCount = itemView.findViewById(R.id.pollCountVote);
            upvote = itemView.findViewById(R.id.pollUpVote);
            downvote = itemView.findViewById(R.id.pollDownVote);
        }
    }
}
