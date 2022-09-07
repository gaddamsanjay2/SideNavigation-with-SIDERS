package com.example.sidenavigationwithsliders.ui.CallLog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sidenavigationwithsliders.R;

import java.util.ArrayList;
import java.util.List;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.CallHolder> {
    Context context;
    List<CallLogModel> callLogModelList=new ArrayList<>();

    public CallLogAdapter(Context context, List<CallLogModel> callLogModelList) {
        this.context = context;
        this.callLogModelList = callLogModelList;
    }

    @NonNull
    @Override
    public CallHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_calllog,parent,false);
        return new CallHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull CallHolder holder, int position) {
        holder.name.setText(callLogModelList.get(position).getName());
        holder.number.setText(callLogModelList.get(position).getNumber());
        holder.time.setText(callLogModelList.get(position).getTime());
        holder.duration.setText(callLogModelList.get(position).getDuration()+"sec");


         holder.type.setText(callLogModelList.get(position).getType());



        if(callLogModelList.get(position).getType().equals("1"))
        {
            holder.type.setText("INCOMING_TYPE");
        }
        else if(callLogModelList.get(position).getType().equals("2"))
        {
            holder.type.setText("OUTGOING_TYPE");
        }
        else  if(callLogModelList.get(position).getType().equals("3"))
        {
            holder.type.setText("MISSED_TYPE");
        }
        else  if(callLogModelList.get(position).getType().equals("4"))
        {
            holder.type.setText("VOICEMAIL_TYPE");
        }
        else if(callLogModelList.get(position).getType().equals("5"))
        {
            holder.type.setText("REJECTED_TYPE");
        }
        else  if(callLogModelList.get(position).getType().equals("6"))
        {
            holder.type.setText("BLOCKED_TYPE");
        }
        else if(callLogModelList.get(position).getType().equals("7"))
        {
            holder.type.setText("ANSWERED_EXTERNALLY_TYPE");
        }
        else
        {
            /*
            * INCOMING_TYPE
OUTGOING_TYPE
MISSED_TYPE
VOICEMAIL_TYPE
REJECTED_TYPE
BLOCKED_TYPE
ANSWERED_EXTERNALLY_TYPE*/
        }
        //setting duration
        String callDuration=callLogModelList.get(position).duration;
        int seconds=Integer.parseInt(callDuration);


        int p1=seconds%60;
        int p2=seconds/60;
        int p3=p2%60;

        if (p3>1){
            holder.duration.setText(p2+"hrs:"+p3+"mins:"+p1+"secs");
        }
        else if (p3==1){
            holder.duration.setText(p3+"mins"+p1+"secs");
        }
        else if (p3==0 && p1==0){
            holder.duration.setText(p1+"secs");
        }else if (p3==0){
            holder.duration.setText(p1+"secs");
        }else {
            holder.duration.setText(p3+"mins"+p1+"secs");
        }
    }

    @Override
    public int getItemCount() {
        return callLogModelList.size();
    }

    public class CallHolder extends RecyclerView.ViewHolder {

        TextView name,number,time,duration,type;
        public CallHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.name);
            number=itemView.findViewById(R.id.number);
            time=itemView.findViewById(R.id.timeStamp);
            duration=itemView.findViewById(R.id.duration);
            type=itemView.findViewById(R.id.type);
        }
    }
}