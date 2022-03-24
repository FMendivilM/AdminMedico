package com.example.adminmedico;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.adminmedico.Entities.Medicine;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class AddMedicine extends AppCompatActivity {

    FirebaseAuth fAuth;
    DatabaseReference databaseReference;
    EditText medNameField, portionField, intervalField, daysField;
    Spinner portionTypeField, admWayField, contactMedField;
    Button createMedBtn, cancelMedBtn;
    String medId, alarmContactNumber, alarmMedName, alarmMedType, alarmMedPortion;
    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
    List<String> contact_number_list = new ArrayList<>();
    String actualTakes = "1";



    int intervals;

    Calendar calendar = Calendar.getInstance();
    Calendar nextTake = Calendar.getInstance();

    final Map<String,Object> medInfo = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean editing = getIntent().getStringExtra("medId") != null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Add Prescription");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        medNameField = findViewById(R.id.tvMedName);
        portionField = findViewById(R.id.tvPortionQuantity);
        intervalField = findViewById(R.id.tvInterval);
        daysField = findViewById(R.id.tvDaysMed);

        portionTypeField = findViewById(R.id.spinnerPortionType);
        admWayField = findViewById(R.id.spinnerAdmWay);
        contactMedField = findViewById(R.id.spinnerContactMed);

        createMedBtn = findViewById(R.id.btn_medCreate);
        cancelMedBtn = findViewById(R.id.btn_medDelete);

        fillSpinners();

        if(editing){
            databaseReference.child("userData").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).child("prescriptions").child(getIntent().getStringExtra("medId")).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        medNameField.setText(Objects.requireNonNull(snapshot.child("medName").getValue()).toString());
                        portionField.setText(Objects.requireNonNull(snapshot.child("portion").getValue()).toString());
                        intervalField.setText(Objects.requireNonNull(snapshot.child("intervals").getValue()).toString());
                        daysField.setText(Objects.requireNonNull(snapshot.child("days").getValue()).toString());
                        actualTakes = Objects.requireNonNull(snapshot.child("actualTakes").getValue()).toString();

                        createMedBtn.setText("Save");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }


        createMedBtn.setOnClickListener(v -> {

            if(TextUtils.isEmpty(medNameField.getText().toString())){
                medNameField.setError("Name is required");
                return;
            }
            if(TextUtils.isEmpty(portionField.getText().toString())){
                portionField.setError("Portion quantity is required");
                return;
            }else if(!TextUtils.isDigitsOnly(portionField.getText().toString())){
                portionField.setError("Invalid data");
                return;
            }

            if(TextUtils.isEmpty(intervalField.getText().toString())){
                intervalField.setError("Hours interval is required");
                return;
            }else if(!TextUtils.isDigitsOnly(intervalField.getText().toString())){
                intervalField.setError("Invalid data");
                return;
            }else if(Integer.parseInt(intervalField.getText().toString()) <= 0){
                intervalField.setError("Intervals can't be zero or less than zero");
                return;
            }

            if(TextUtils.isEmpty(daysField.getText().toString())){
                daysField.setError("Number of days is required");
                return;
            }else if(!TextUtils.isDigitsOnly(daysField.getText().toString())){
                daysField.setError("Invalid data");
                return;
            }else if(Integer.parseInt(daysField.getText().toString()) <= 0){
                daysField.setError("Days can't be zero or less than zero");
                return;
            }

            if(Float.parseFloat(intervalField.getText().toString())/24 > Integer.parseInt(daysField.getText().toString())){
                intervalField.setError("Intervals cannot be greater than the number of days");
                return;
            }

            medId = editing? getIntent().getStringExtra("medId") : String.valueOf((int) System.currentTimeMillis());

            nextTake.add(Calendar.MINUTE, Integer.parseInt(intervalField.getText().toString()));
            nextTake.set(Calendar.SECOND , 0);
            Date date = calendar.getTime();
            Date nextDate  = nextTake.getTime();
            int totalTakes = (Integer.parseInt(daysField.getText().toString()) * 24) / Integer.parseInt(intervalField.getText().toString()) ;
            Medicine med = new Medicine(
                    medId,
                    medNameField.getText().toString(),
                    Integer.parseInt(portionField.getText().toString()),
                    portionTypeField.getSelectedItem().toString(),
                    intervalField.getText().toString(),
                    daysField.getText().toString(),
                    dateFormat.format(date),
                    admWayField.getSelectedItem().toString(),
                    contactMedField.getSelectedItem().toString(),
                    contact_number_list.get(contactMedField.getSelectedItemPosition()),
                    Integer.parseInt(actualTakes),
                    totalTakes,
                    null
            );

            alarmContactNumber = med.getContactNumber();
            alarmMedName = med.getName();
            alarmMedType = med.getPortionType();
            alarmMedPortion = String.valueOf(med.getPortion());
            intervals = Integer.parseInt(med.getIntervals());

            medInfo.put("id", med.getId());
            medInfo.put("medName", med.getName());
            medInfo.put("portion", med.getPortion());
            medInfo.put("portionType", med.getPortionType());
            medInfo.put("intervals", med.getIntervals());
            medInfo.put("days", med.getDays());
            medInfo.put("startingHour", med.getHour());
            medInfo.put("nextTakeHour", dateFormat.format(nextDate));
            medInfo.put("via", med.getVia());
            medInfo.put("medContactName", med.getContactName());
            medInfo.put("medContactNumber", med.getContactNumber());
            medInfo.put("actualTakes", med.getActualTakes());
            medInfo.put("totalTakes", med.getTotalTakes());



            databaseReference.child("userData").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(editing){
                        databaseReference.child("userData").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).child("prescriptions").child(med.getId()).removeValue();
                    }
                    databaseReference.child("userData").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).child("prescriptions").child(med.getId()).setValue(medInfo).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            createAlarm();
                            finish();
                        }
                    });
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });

        cancelMedBtn.setOnClickListener(v -> finish());
    }

    void fillSpinners(){
        ArrayAdapter<CharSequence> portionType = ArrayAdapter.createFromResource(this, R.array.portion_type, android.R.layout.simple_spinner_item);
        portionType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        portionTypeField.setAdapter(portionType);

        ArrayAdapter<CharSequence> admWay = ArrayAdapter.createFromResource(this, R.array.adm_way, android.R.layout.simple_spinner_item);
        admWay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        admWayField.setAdapter(admWay);

        contactSpinner();
    }

    void contactSpinner(){
        final List<String> contact_list = new ArrayList<>();
        final String[] contactName = new String[1];
        final String[] contactNumber = new String[1];
        contact_list.add("None");
        contact_number_list.add("");
        databaseReference.child("userData").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child("contacts").exists()){
                    if(snapshot.hasChildren()){
                        for (DataSnapshot objSnapshot : snapshot.child("contacts").getChildren()) {
                            contactName[0] = Objects.requireNonNull(objSnapshot.child("name").getValue()).toString();
                            contactNumber[0] = Objects.requireNonNull(objSnapshot.child("number").getValue()).toString();
                            contact_list.add(contactName[0]);
                            contact_number_list.add(contactNumber[0]);
                        }
                    }
                }
                ArrayAdapter<String> contactMed = new ArrayAdapter<>(AddMedicine.this, android.R.layout.simple_spinner_item, contact_list);
                contactMed.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                contactMedField.setAdapter(contactMed);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void createAlarm(){
        Intent i = new Intent(getApplicationContext(), AlarmReceiver.class);
        i.putExtra("medId", medId);
        i.putExtra("contactNumber", alarmContactNumber);
        i.putExtra("medName", alarmMedName);
        i.putExtra("medType", alarmMedType);
        i.putExtra("medPortion", alarmMedPortion);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(medId), i, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager;
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, nextTake.getTimeInMillis(), (long) intervals*60*1000, pendingIntent);

    }


}