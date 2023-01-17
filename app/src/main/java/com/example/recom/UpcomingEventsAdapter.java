package com.example.recom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpcomingEventsAdapter extends RecyclerView.Adapter<UpcomingEventsAdapter.EventViewHolder> {
    private List<Event> events;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", new Locale("en", "PH"));
    public UpcomingEventsAdapter(List<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public UpcomingEventsAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.preview_monthsched, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingEventsAdapter.EventViewHolder holder, int position) {
        Event event = events.get(position);

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        try {
            Date eventDate = sdf.parse(event.getDate());

            //get day of the week such as monday tues wed etc...
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("en", "PH"));
            String dayOfWeek = dayFormat.format(eventDate);
            holder.day.setText(dayOfWeek);

            SimpleDateFormat dayGet = new SimpleDateFormat("dd", new Locale("en", "PH"));
            String day = dayGet.format(eventDate);
            holder.date.setText(day);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        holder.title.setText(event.getName());
        holder.description.setText(event.getDescription());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder{
        public TextView date, day, title, description;
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            description = itemView.findViewById(R.id.EDescription);
            title = itemView.findViewById(R.id.ETitle);
            day = itemView.findViewById(R.id.EDay);
            date = itemView.findViewById(R.id.EDate);
        }
    }
}
