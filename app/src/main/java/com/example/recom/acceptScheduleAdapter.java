package com.example.recom;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class acceptScheduleAdapter extends RecyclerView.Adapter<acceptScheduleAdapter.ViewHolder> {
    private ArrayList<Event> list;
    private ArrayList<String> pushKey;

    public acceptScheduleAdapter(ArrayList<Event> list, ArrayList<String> pushKey) {
        this.list = list;
        this.pushKey = pushKey;
    }

    @NonNull
    @Override
    public acceptScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_acceptschedulerecycler, parent, false);
        return new acceptScheduleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull acceptScheduleAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Event event = list.get(position);

        holder.activitySched.setText(event.getBorrowName());
        holder.schedTime.setText(event.getStartTime() + " - " + event.getEndTime());
        holder.acceptSched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptingSchedule fragment = new acceptingSchedule();
                Bundle bundle = new Bundle();
                bundle.putString("pushkey", pushKey.get(position));

                fragment.setArguments(bundle);
                FragmentManager fragmentManager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout acceptSched;
        TextView activitySched, schedTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            acceptSched = itemView.findViewById(R.id.acceptSched);
            activitySched = itemView.findViewById(R.id.activitySched);
            schedTime = itemView.findViewById(R.id.schedtime);
        }
    }
}
