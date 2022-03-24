package com.example.adminmedico.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.adminmedico.Login;
import com.example.adminmedico.R;
import com.example.adminmedico.UserUpdate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UserFragment extends Fragment {


    TextView nameTv;
    TextView mailTv;
    TextView passwordTv;
    Switch hideSwitch;
    Button editButton;
    Button deleteButton;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    DatabaseReference db;
    String mail = "", password ="";
    String userId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        nameTv = view.findViewById(R.id.tv_user_name);
        mailTv = view.findViewById(R.id.tv_user_mail);
        passwordTv = view.findViewById(R.id.tv_user_password);
        hideSwitch = view.findViewById(R.id.switch_user_hide);
        deleteButton = view.findViewById(R.id.btn_user_delete);
        editButton = view.findViewById(R.id.btn_user_edit);
        hideSwitch.setChecked(true);
        hideSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                passwordTv.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }else{
                passwordTv.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });


        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();

        db = FirebaseDatabase.getInstance().getReference();
        userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        loadData();

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(this.getContext()));
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure? All your data will be deleted");
        builder.setPositiveButton("YES", (dialog, which) -> {
            db.child("userData").child(userId).child("data").removeValue();
            Objects.requireNonNull(fUser).delete().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Intent i = new Intent(getContext(), Login.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    Objects.requireNonNull(this.getActivity()).finish();
                }
            });

            dialog.dismiss();
        });
        builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
        AlertDialog alert = builder.create();

        deleteButton.setOnClickListener(v -> alert.show());

        editButton.setOnClickListener(v ->{
            Intent i = new Intent(getContext(), UserUpdate.class);
            i.putExtra("userName", nameTv.getText().toString());
            i.putExtra("email", mailTv.getText().toString());
            i.putExtra("password", passwordTv.getText().toString());

            startActivity(i);
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    void loadData(){
        db.child("userData").child(userId).child("data").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    nameTv.setText(Objects.requireNonNull(snapshot.child("userName").getValue()).toString());
                    mailTv.setText(Objects.requireNonNull(snapshot.child("email").getValue()).toString());
                    passwordTv.setText(Objects.requireNonNull(snapshot.child("password").getValue()).toString());
                    mail = mailTv.getText().toString();
                    password = passwordTv.getText().toString();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}