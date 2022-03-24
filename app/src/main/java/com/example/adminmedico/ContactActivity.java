package com.example.adminmedico;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ContactActivity extends AppCompatActivity {

    TextView mName, mNumber;
    Button mEdit, mDelete;
    DatabaseReference db;
    FirebaseAuth fAuth;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        mName = findViewById(R.id.tv_name);
        mNumber = findViewById(R.id.tv_number);
        mEdit = findViewById(R.id.btn_edit);
        mDelete = findViewById(R.id.btn_delete);
        db = FirebaseDatabase.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();

        Objects.requireNonNull(getSupportActionBar()).setTitle("Contact");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        number = "";
        if(getIntent().getStringExtra("contactNumber") != null){
            number = getIntent().getStringExtra("contactNumber");
        }

        db.child("userData").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).child("contacts").child(number).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    mName.setText(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                    mNumber.setText(Objects.requireNonNull(snapshot.child("number").getValue()).toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        mEdit.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), AddContact.class);
            i.putExtra("name", mName.getText().toString());
            i.putExtra("number", mNumber.getText().toString());
            startActivity(i);
            finish();

        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", (dialog, which) -> {
            number = mNumber.getText().toString();
            mName.setText("");
            mNumber.setText("");
            db.child("userData").child(fAuth.getCurrentUser().getUid()).child("contacts").child(number).removeValue();
            dialog.dismiss();
            finish();
        });
        builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());

        AlertDialog alert = builder.create();

        mDelete.setOnClickListener(v -> alert.show());

    }
}