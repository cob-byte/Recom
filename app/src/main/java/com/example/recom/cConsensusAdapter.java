package com.example.recom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class cConsensusAdapter extends RecyclerView.Adapter<cConsensusAdapter.MyViewHolder> {
    Context context;
    ArrayList<cConsensus> list;
    ArrayList<String> pushKey;

    public cConsensusAdapter(Context context, ArrayList<cConsensus> list, ArrayList<String> pushKey) {
        this.context = context;
        this.list = list;
        this.pushKey = pushKey;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comconsensus, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        cConsensus consensus = list.get(position);
        holder.pollTitle.setText(consensus.getTitle());
        holder.pollDescription.setSelected(true);
        holder.pollDescription.setText(consensus.getQuestion());
        String dateTime = consensus.getDate() + " " + consensus.getTime();
        holder.pollDateTime.setText(dateTime);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewPoll = new Intent(context, Viewpoll.class);
                viewPoll.putExtra("pushKey", pushKey.get(position));
                viewPoll.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(viewPoll);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public RelativeLayout relativeLayout;
        public TextView pollTitle;
        public TextView pollDescription;
        public TextView pollDateTime;
        public ImageButton viewPoll;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.poll);
            pollTitle = itemView.findViewById(R.id.pollTitle);
            pollDescription = itemView.findViewById(R.id.pollDescription);
            pollDateTime = itemView.findViewById(R.id.pollDateTime);
            viewPoll = itemView.findViewById(R.id.seePoll);
        }
    }
}