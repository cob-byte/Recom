package com.example.recom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class bConsensusAdapter extends RecyclerView.Adapter<bConsensusAdapter.ViewHolder> {
    Context context;
    ArrayList<bConsensus> list;
    ArrayList<String> pushKey;

    public bConsensusAdapter(Context context, ArrayList<bConsensus> list, ArrayList<String> pushKey) {
        this.context = context;
        this.list = list;
        this.pushKey = pushKey;
    }

    @NonNull
    @Override
    public bConsensusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comconsensus, parent, false);
        return new bConsensusAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull bConsensusAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        bConsensus bconsensus = list.get(position);
        holder.pollTitle.setText(bconsensus.getTitle());
        holder.pollDescription.setSelected(true);
        holder.pollDescription.setText(bconsensus.getQuestion());
        String dateTime = bconsensus.getDate() + " " + bconsensus.getTime();
        holder.pollDateTime.setText(dateTime);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewPoll = new Intent(context, brgyViewPoll.class);
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

    public class ViewHolder extends RecyclerView.ViewHolder{
        public RelativeLayout relativeLayout;
        public TextView pollTitle;
        public TextView pollDescription;
        public TextView pollDateTime;
        public ImageButton viewPoll;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.poll);
            pollTitle = itemView.findViewById(R.id.pollTitle);
            pollDescription = itemView.findViewById(R.id.pollDescription);
            pollDateTime = itemView.findViewById(R.id.pollDateTime);
            viewPoll = itemView.findViewById(R.id.seePoll);
        }
    }
}
