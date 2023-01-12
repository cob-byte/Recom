package com.example.recom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class allPollsAdapter extends RecyclerView.Adapter<allPollsAdapter.MyViewHolder>{
    Context context;
    ArrayList<cConsensus> allList;
    ArrayList<String> pushKey;

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
