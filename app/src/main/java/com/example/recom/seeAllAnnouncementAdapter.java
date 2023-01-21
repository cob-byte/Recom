package com.example.recom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class seeAllAnnouncementAdapter extends RecyclerView.Adapter<seeAllAnnouncementAdapter.ViewHolder> {

    ArrayList<Announcement> pagerArrayList;

    public void setData(ArrayList<Announcement> pagerArrayList){
        this.pagerArrayList = pagerArrayList;
        notifyDataSetChanged();
    }

    public seeAllAnnouncementAdapter(ArrayList<Announcement> pagerArrayList){
        this.pagerArrayList = pagerArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.see_allpolllayout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Announcement announcement = pagerArrayList.get(position);

        Picasso.get().load(announcement.getImageUri()).into(holder.imageView);
        holder.tcHeading.setText(announcement.getTypeTitle());
        holder.tvDesc.setText(announcement.getTypeDescription());
        holder.name.setText(announcement.getName());
        holder.dateTime.setText(announcement.getCurrentDateTime());
        holder.desc.setText(announcement.getTypeImageDesc());
        Picasso.get().load(announcement.getImage()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return pagerArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView image;
        ImageView imageView;
        TextView tcHeading, tvDesc, name, dateTime, desc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.ivimage);
            tcHeading = itemView.findViewById(R.id.tvHeading);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            name = itemView.findViewById(R.id.AnnAuthor);
            image = itemView.findViewById(R.id.AnnAuthorProfile);
            dateTime = itemView.findViewById(R.id.AnnTime);
            desc = itemView.findViewById(R.id.imageDescription);
        }
    }
}
