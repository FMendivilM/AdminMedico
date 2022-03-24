package com.example.adminmedico;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AlarmActivity extends AppCompatActivity {

    TextView medInfoTv;
    Button confirmAlarmBtn;
    FirebaseAuth fAuth;
    DatabaseReference db;
    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);

    String medName, portionType, portion, totalTakes = "", actualTakes = "", nexTake = "",
            hour ="", minute="";
    int actualTakesInt = 0, totalTakesInt = 0, intervals = 0;

    String message;
    String medId;

    Date nextTakeHour;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {1000, 1000, 1000, 1000};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
        } else {
            vibrator.vibrate(pattern, 0);
        }

        Ringtone ringtone = RingtoneManager.getRingtone(this,
                Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.alarm_tone));
        ringtone.play();

        db = FirebaseDatabase.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();

        medInfoTv = findViewById(R.id.tv_med_info);
        confirmAlarmBtn = findViewById(R.id.btn_take_alarm);
        medId = getIntent().getStringExtra("medId");

        db.child("userData").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).child("prescriptions").child(medId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    medName = Objects.requireNonNull(snapshot.child("medName").getValue()).toString();
                    portionType = Objects.requireNonNull(snapshot.child("portionType").getValue()).toString();
                    portion = Objects.requireNonNull(snapshot.child("portion").getValue()).toString();
                    totalTakes = Objects.requireNonNull(snapshot.child("totalTakes").getValue()).toString();
                    actualTakes = Objects.requireNonNull(snapshot.child("actualTakes").getValue()).toString();
                    nexTake = Objects.requireNonNull(snapshot.child("nextTakeHour").getValue()).toString();
                    intervals = Integer.parseInt(Objects.requireNonNull(snapshot.child("intervals").getValue()).toString());

                    totalTakesInt = Integer.parseInt(totalTakes);
                    actualTakesInt = Integer.parseInt(actualTakes);

                    hour = nexTake.substring(0,2);
                    minute = nexTake.substring(3,5);
                    message = medName + " " + portion + " " + portionType;

                    medInfoTv.setText(message);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });




        confirmAlarmBtn.setOnClickListener(v->{
            vibrator.cancel();
            ringtone.stop();

            actualTakesInt++;
            boolean medComplete = actualTakesInt >= totalTakesInt;

            calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, intervals);
            calendar.set(Calendar.SECOND, 0);
            Date nextTakeHour = calendar.getTime();

            String nextTakeFormat = dateFormat.format(nextTakeHour);

            db.child("userData").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).child("prescriptions").child(medId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        if(medComplete){
                            db.child("userData").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid())
                                    .child("prescriptions").child(medId).removeValue();
                            Intent i = new Intent(getApplicationContext(), AlarmReceiver.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(medId), i, PendingIntent.FLAG_UPDATE_CURRENT);
                            AlarmManager alarmManager;
                            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            alarmManager.cancel(pendingIntent);
                        }else{
                            actualTakes = String.valueOf(actualTakesInt);
                            db.child("userData").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid())
                                    .child("prescriptions").child(medId).child("actualTakes").setValue(actualTakes);

                            db.child("userData").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid())
                                    .child("prescriptions").child(medId).child("nextTakeHour").setValue(nextTakeFormat);

                            //createAlarm();
                        }
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            finish();
        });
    }


}