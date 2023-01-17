package com.example.recom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HotlineMobileNumAdapter extends RecyclerView.Adapter<HotlineMobileNumAdapter.MyViewHolder> {
    //HotlineAllNumber Adapter Class
    private Context context3;
    private List<HotlineInfo> mhotlineInfos;

    //constructor

    public HotlineMobileNumAdapter(Context context3, List<HotlineInfo> mhotlineInfos) {
        this.context3 = context3;
        this.mhotlineInfos = mhotlineInfos;
    }

    @NonNull
    @Override
    public HotlineMobileNumAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context3).inflate(R.layout.mhotline_recycler,parent,false);
        return new HotlineMobileNumAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotlineMobileNumAdapter.MyViewHolder holder, int position) {
        holder.HotlineName.setText(mhotlineInfos.get(position).getPlace());
        holder.HotlineNumber.setText(mhotlineInfos.get(position).getContact());

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
        return mhotlineInfos.size();
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
