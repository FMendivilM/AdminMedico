package com.example.adminmedico.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminmedico.Entities.Contact;
import com.example.adminmedico.R;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements View.OnClickListener {

    public ArrayList<Contact> model;
    LayoutInflater inflater;

    //listener
    private View.OnClickListener listener;

    public ContactAdapter(Context context, ArrayList<Contact> model){
        this.inflater = LayoutInflater.from(context);
        this. model = model;


    }

    @Override
    public void onClick(View v) {
        if(listener !=null){
            listener.onClick(v);
        }
    }

    public static class  ViewHolder extends RecyclerView.ViewHolder{

        TextView names,numbers;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            names = itemView.findViewById(R.id.medHour);
            numbers = itemView.findViewById(R.id.medName);
        }
    }
    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_contacts, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    public void setOnclickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder holder, int position) {
        String name = model.get(position).getName();
        String number = model.get(position).getNumber();
        holder.names.setText(name);
        holder.numbers.setText(number);
    }

    @Override
    public int getItemCount() { return model.size(); }
}
