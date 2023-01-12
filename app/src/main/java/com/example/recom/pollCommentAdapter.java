package com.example.recom;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class pollCommentAdapter extends RecyclerView.Adapter<pollCommentAdapter.MyViewHolder> {
    Context context;
    ArrayList<pollComment> comment;

    public pollCommentAdapter(Context context, ArrayList<pollComment> comment) {
        this.context = context;
        this.comment = comment;
    }

    @NonNull
    @Override
    public pollCommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.poll_comments, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull pollCommentAdapter.MyViewHolder holder, int position) {
        pollComment pollcomment = comment.get(position);
        if(pollcomment.getImage() != null){
            Picasso.get().load(pollcomment.getImage()).into(holder.profileImage);
        }

        holder.commentAuthor.setText(pollcomment.getName());
        holder.commentDescription.setText(pollcomment.getComment());
        String dateTime = pollcomment.getDate() + " " + pollcomment.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy HH:mm");
        try {
            long time = sdf.parse(dateTime).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            holder.commentDateTime.setText(ago);
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return comment.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout commentsLayout;
        CircleImageView profileImage;
        TextView commentAuthor;
        TextView commentDateTime;
        TextView commentDescription;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            commentsLayout = itemView.findViewById(R.id.commentsLayout);
            profileImage = itemView.findViewById(R.id.profileImage);
            commentAuthor = itemView.findViewById(R.id.commentAuthor);
            commentDateTime = itemView.findViewById(R.id.commentDateTime);
            commentDescription = itemView.findViewById(R.id.commentDescription);
        }
    }
}
