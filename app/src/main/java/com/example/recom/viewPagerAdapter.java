package com.example.recom;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class viewPagerAdapter extends RecyclerView.Adapter<viewPagerAdapter.ViewHolder> {

    ArrayList<Announcement> pagerArrayList;

    public void setData(ArrayList<Announcement> pagerArrayList){
        this.pagerArrayList = pagerArrayList;
        notifyDataSetChanged();
    }

    public viewPagerAdapter(ArrayList<Announcement> pagerArrayList){
        this.pagerArrayList = pagerArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_welcome,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Announcement announcement = pagerArrayList.get(position);

        Picasso.get().load(announcement.getImageUri()).into(holder.imageView);
        holder.tcHeading.setText(announcement.getTypeTitle());
        holder.tvDesc.setText(announcement.getTypeDescription());
        holder.timeDate.setText(announcement.getCurrentDateTime());

    }

    @Override
    public int getItemCount() {
        return pagerArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView tcHeading, tvDesc, timeDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.ivimage);
            tcHeading = itemView.findViewById(R.id.tvHeading);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            timeDate = itemView.findViewById(R.id.AnnTime);
        }
    }
}
