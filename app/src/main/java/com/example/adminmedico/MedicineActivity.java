package com.example.adminmedico;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
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

public class MedicineActivity extends AppCompatActivity {

    TextView medName, portion, portionType, intervals, days, admWay, medContact;
    Button medEdit, medDelete;

    FirebaseAuth fAuth;
    DatabaseReference db;
    String medId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Prescription");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        medName = findViewById(R.id.tv_medName);
        portion = findViewById(R.id.tv_portion);
        portionType = findViewById(R.id.tv_portionType);
        intervals = findViewById(R.id.tv_interval);
        days = findViewById(R.id.tv_days);
        admWay = findViewById(R.id.tv_admWay);
        medContact = findViewById(R.id.tv_medContact);

        medEdit = findViewById(R.id.btn_medEdit);
        medDelete = findViewById(R.id.btn_medDelete);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        if(getIntent().getStringExtra("medId") != null){
            medId = getIntent().getStringExtra("medId");
        }

        db.child("userData").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).child("prescriptions").child(medId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    medName.setText(Objects.requireNonNull(snapshot.child("medName").getValue()).toString());
                    portion.setText(Objects.requireNonNull(snapshot.child("portion").getValue()).toString());
                    portionType.setText(Objects.requireNonNull(snapshot.child("portionType").getValue()).toString());
                    intervals.setText(Objects.requireNonNull(snapshot.child("intervals").getValue()).toString());
                    days.setText(Objects.requireNonNull(snapshot.child("days").getValue()).toString());
                    admWay.setText(Objects.requireNonNull(snapshot.child("via").getValue()).toString());
                    medContact.setText(Objects.requireNonNull(snapshot.child("medContactName").getValue()).toString());

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        medEdit.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), AddMedicine.class);
            i.putExtra("medId", medId);
            startActivity(i);
            finish();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", (dialog, which) -> {

            db.child("userData").child(fAuth.getCurrentUser().getUid()).child("prescriptions").child(medId).removeValue();

            Intent i = new Intent(getApplicationContext(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(medId), i, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager;
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            dialog.dismiss();
            finish();
        });
        builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
        AlertDialog alert = builder.create();

        medDelete.setOnClickListener(v -> alert.show());


    }
}