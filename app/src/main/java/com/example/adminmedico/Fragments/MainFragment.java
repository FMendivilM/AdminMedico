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

import com.example.adminmedico.Adapters.MainAdapter;
import com.example.adminmedico.Adapters.MedicineAdapter;
import com.example.adminmedico.Entities.Medicine;
import com.example.adminmedico.MedicineActivity;
import com.example.adminmedico.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MainFragment extends Fragment {

    private Context mContext;
    MainAdapter mainAdapter;
    RecyclerView recyclerViewMain;
    ArrayList<Medicine> medicineListMain;
    FirebaseAuth fAuth;
    DatabaseReference databaseReference;
    TextView emptyListTv;

    Activity activity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity) {
            this.activity = (Activity) context;
            mContext = context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_main,container,false);

        recyclerViewMain = view.findViewById(R.id.recyclerViewMain);
        emptyListTv = view.findViewById(R.id.tv_empty_list);
        fAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        medicineListMain = new ArrayList<>();

        loadList();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadList();
    }

    public void loadList(){
        databaseReference.child("userData").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).child("prescriptions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                medicineListMain.clear();
                for(DataSnapshot objSnapshot: snapshot.getChildren()){
                    medicineListMain.add(new Medicine(
                            objSnapshot.getKey(),
                            Objects.requireNonNull(objSnapshot.child("medName").getValue()).toString(),
                            Objects.requireNonNull(objSnapshot.child("nextTakeHour").getValue()).toString(),
                            Integer.parseInt(Objects.requireNonNull(objSnapshot.child("actualTakes").getValue()).toString()),
                            Integer.parseInt(Objects.requireNonNull(objSnapshot.child("totalTakes").getValue()).toString())));
                }
                if(medicineListMain.size() > 0){
                    emptyListTv.setText("");
                }else{
                    emptyListTv.setText("No pending alarms");
                }
                showData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    public void showData(){
        recyclerViewMain.setLayoutManager(new LinearLayoutManager(getActivity()));
        mainAdapter = new MainAdapter(mContext, medicineListMain);
        recyclerViewMain.setAdapter(mainAdapter);

        mainAdapter.setOnclickListener(v ->{
            Intent i = new Intent(mContext, MedicineActivity.class);
            i.putExtra("medId", medicineListMain.get(recyclerViewMain.getChildAdapterPosition(v)).getId());
            startActivity(i);
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }


}
