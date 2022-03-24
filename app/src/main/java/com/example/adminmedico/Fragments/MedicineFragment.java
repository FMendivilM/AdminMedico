package com.example.adminmedico.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminmedico.Adapters.MedicineAdapter;
import com.example.adminmedico.AddMedicine;
import com.example.adminmedico.Entities.Medicine;
import com.example.adminmedico.MedicineActivity;
import com.example.adminmedico.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class MedicineFragment extends Fragment {


    MedicineAdapter medicineAdapter;
    RecyclerView recyclerViewMedicine;
    ArrayList<Medicine> medicineList;
    FloatingActionButton btnAddMed;
    FirebaseAuth fAuth;
    DatabaseReference databaseReference;
    TextView emptyListMedTv;

    private Context mContext;
    Activity activity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity) {
            this.activity = (Activity) context;
            mContext = context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicine,container,false);
        recyclerViewMedicine = view.findViewById(R.id.recyclerViewMedicine);
        medicineList = new ArrayList<>();
        fAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        btnAddMed = view.findViewById(R.id.btnAddMedicine);

        emptyListMedTv = view.findViewById(R.id.tv_empty_list_medicine);
        loadList();


        btnAddMed.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), AddMedicine.class);
            startActivity(i);
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }



    public void loadList(){
        databaseReference.child("userData").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).child("prescriptions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                medicineList.clear();
                for(DataSnapshot objSnapshot: snapshot.getChildren()){
                    medicineList.add(new Medicine(
                            objSnapshot.getKey(),
                            Objects.requireNonNull(objSnapshot.child("medName").getValue()).toString(),
                            Objects.requireNonNull(objSnapshot.child("nextTakeHour").getValue()).toString(),
                            Integer.parseInt(Objects.requireNonNull(objSnapshot.child("actualTakes").getValue()).toString()),
                            Integer.parseInt(Objects.requireNonNull(objSnapshot.child("totalTakes").getValue()).toString())));
                }
                if(medicineList.size() > 0){
                    emptyListMedTv.setText("");
                }else{
                    emptyListMedTv.setText("No registered alarms");
                }
                showData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        loadList();
    }

    public void showData(){
        recyclerViewMedicine.setLayoutManager(new LinearLayoutManager(getActivity()));
        medicineAdapter = new MedicineAdapter(mContext, medicineList);
        recyclerViewMedicine.setAdapter(medicineAdapter);

        medicineAdapter.setOnclickListener(v ->{
            Intent i = new Intent(mContext, MedicineActivity.class);
            i.putExtra("medId", medicineList.get(recyclerViewMedicine.getChildAdapterPosition(v)).getId());
            startActivity(i);
        });
    }
}