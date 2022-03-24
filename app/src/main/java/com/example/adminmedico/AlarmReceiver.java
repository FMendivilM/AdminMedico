package com.example.adminmedico;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {

    FirebaseAuth fAuth;
    DatabaseReference db;
    String contactNumber, medName, medType, medPortion;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override

    public void onReceive(Context context, Intent intent) {


        db = FirebaseDatabase.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();

        SmsManager smsManager = SmsManager.getDefault();
        String medId = intent.getStringExtra("medId");
        contactNumber = intent.getStringExtra("contactNumber");
        medName = intent.getStringExtra("medName");
        medType = intent.getStringExtra("medType");
        medPortion = intent.getStringExtra("medPortion");

        db.child("userData").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).child("data").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = Objects.requireNonNull(snapshot.child("userName").getValue()).toString();

                String message = userName + " has to take " + medName + " " + medPortion + " " + medType;
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {

                    if(!contactNumber.equals("")){
                        smsManager.sendTextMessage(contactNumber,
                                null, message, null, null);
                        Toast.makeText(context, "SMS Sent", Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        Intent i = new Intent(context, AlarmActivity.class);
        i.putExtra("medId", medId);


        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

}
