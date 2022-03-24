package com.example.adminmedico.Adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.adminmedico.Entities.Medicine;
import com.example.adminmedico.R;

import java.util.ArrayList;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder> implements View.OnClickListener {

    public ArrayList<Medicine> model;
    LayoutInflater inflater;

    //listener
    private View.OnClickListener listener;

    public MedicineAdapter(Context context, ArrayList<Medicine> model){
        this.inflater = LayoutInflater.from(context);
        this.model = model;


    }

    @Override
    public void onClick(View v) {
        if(listener !=null){
            listener.onClick(v);
        }
    }

    public static class  ViewHolder extends RecyclerView.ViewHolder{

        TextView medHourTv,medNameTv, progressTv;
        ProgressBar medPb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            medHourTv = itemView.findViewById(R.id.list_med_hour);
            medNameTv = itemView.findViewById(R.id.list_med_name);
            progressTv = itemView.findViewById(R.id.tv_progress);
            medPb = itemView.findViewById(R.id.pb_med);
        }
    }
    @NonNull
    @Override
    public MedicineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_medicine, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }


    public void setOnclickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineAdapter.ViewHolder holder, int position) {
        String medName = model.get(position).getName();
        String medHour = model.get(position).getHour();
        holder.medHourTv.setText(medHour);
        holder.medNameTv.setText(medName);
        holder.medPb.setMax(model.get(position).getTotalTakes());
        holder.medPb.setProgress(model.get(position).getActualTakes());
        String progress = model.get(position).getActualTakes() + "/" + model.get(position).getTotalTakes();
        holder.progressTv.setText(progress);
    }

    @Override
    public int getItemCount() {
        return model.size();
    }
}