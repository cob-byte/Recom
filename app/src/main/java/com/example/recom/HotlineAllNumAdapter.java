package com.example.recom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HotlineAllNumAdapter extends RecyclerView.Adapter<HotlineAllNumAdapter.MyViewHolder> {

    //HotlineAllNumber Adapter Class
    private Context context2;
    private List<HotlineInfo> hotlineInfos;

    //constructor

    public HotlineAllNumAdapter(Context context2, List<HotlineInfo> hotlineInfos) {
        this.context2 = context2;
        this.hotlineInfos = hotlineInfos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context2).inflate(R.layout.hotline_recycler,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.HotlineName.setText(hotlineInfos.get(position).getPlace());
        holder.HotlineNumber.setText(hotlineInfos.get(position).getContact());

        //holder.HotlineNumber.setOnClickListener(new View.OnClickListener() {
        //@Override
        //public void onClick(View view) {
        //Intent intent = new Intent(context, kung san pupunta.class);

        //context.startActivity(intent);
        //}
        //});
    }

    @Override
    public int getItemCount() {
        return hotlineInfos.size();
    }


    //View Holder: MyView holder
    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView HotlineNumber, HotlineName;

        public MyViewHolder(View itemView){
            super(itemView);
            HotlineName =itemView.findViewById(R.id.HotlineName);
            HotlineNumber=itemView.findViewById(R.id.HotlineNumber);

        }
    }
}
