package com.example.adminmedico.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminmedico.Entities.Medicine;
import com.example.adminmedico.R;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> implements View.OnClickListener {

    public ArrayList<Medicine> model;
    LayoutInflater inflater;

    //listener
    private View.OnClickListener listener;

    public MainAdapter(Context context, ArrayList<Medicine> model){
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

        TextView medHourTv,medNameTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            medHourTv = itemView.findViewById(R.id.list_main_med_hour);
            medNameTv = itemView.findViewById(R.id.list_main_med_name);
        }
    }
    @NonNull
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_main, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }


    public void setOnclickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
        String medName = model.get(position).getName();
        String medHour = model.get(position).getHour();
        holder.medHourTv.setText(medHour);
        holder.medNameTv.setText(medName);
    }

    @Override
    public int getItemCount() {
        return model.size();
    }
}