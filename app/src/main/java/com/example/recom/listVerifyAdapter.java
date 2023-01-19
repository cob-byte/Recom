package com.example.recom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.core.Context;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class listVerifyAdapter extends RecyclerView.Adapter<listVerifyAdapter.ViewHolder> {
    ArrayList<verifyInfo> list;

    public listVerifyAdapter(ArrayList<verifyInfo> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public listVerifyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list, parent, false);
        return new listVerifyAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull listVerifyAdapter.ViewHolder holder, int position) {
        verifyInfo verifyInfo = list.get(position);

        if(verifyInfo.getPhotourl() != null){
            Picasso.get().load(verifyInfo.getPhotourl()).into(holder.profileImage);
        }

        holder.name.setText(verifyInfo.getName());

        holder.seeRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewInfo fragment = new reviewInfo();
                Bundle bundle = new Bundle();
                bundle.putString("votersCert", verifyInfo.getVotersCert());
                bundle.putString("brgyCert", verifyInfo.getBrgyCert());
                bundle.putString("name", verifyInfo.getName());
                bundle.putString("uid", verifyInfo.getUid());
                if(verifyInfo.getPhotourl() != null){
                    bundle.putString("photourl", verifyInfo.getPhotourl());
                }
                else{
                    bundle.putString("photourl", "empty");
                }
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
        CircleImageView profileImage;
        TextView name;
        ImageButton seeRequest;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);
            name = itemView.findViewById(R.id.userName);
            seeRequest = itemView.findViewById(R.id.viewRequest);
        }
    }
}
