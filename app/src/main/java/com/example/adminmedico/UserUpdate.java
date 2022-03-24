package com.example.adminmedico;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserUpdate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update);

        Objects.requireNonNull(getSupportActionBar()).setTitle("User");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Map<String,Object> userInfo = new HashMap<>();

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        String name = getIntent().getStringExtra("userName");
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");

        EditText editUserField = findViewById(R.id.edit_user_field);
        EditText editEmailField = findViewById(R.id.edit_email_field);
        EditText editPasswordField = findViewById(R.id.edit_password_field);
        EditText editConfirmPasswordField = findViewById(R.id.edit_confirm_password);

        Button userSaveBtn = findViewById(R.id.btn_user_save);
        Button userCancelBtn = findViewById(R.id.btn_user_cancel);

        editUserField.setText(name);
        editEmailField.setText(email);
        editPasswordField.setText(password);
        editConfirmPasswordField.setText(password);

        userSaveBtn.setOnClickListener(v->{

            if(TextUtils.isEmpty(editUserField.getText().toString()))
            {
                editUserField. setError("User name is required");
                return;
            }

            if(TextUtils.isEmpty(editEmailField.getText().toString()))
            {
                editEmailField. setError("Email is required");
                return;
            }

            if(TextUtils.isEmpty(editPasswordField.getText().toString().trim()) || TextUtils.isEmpty(editConfirmPasswordField.getText().toString().trim()))
            {
                editPasswordField.setError("Password is required");
                return;
            }

            if(editPasswordField.getText().toString().trim().length() < 6)
            {
                editPasswordField.setError("Password must be 6 characters or longer");
            }

            if(!editPasswordField.getText().toString().trim().equals(editConfirmPasswordField.getText().toString().trim()))
            {
                editPasswordField.setError("Password mismatch");
                return;
            }
            userInfo.put("userName", editUserField.getText().toString().trim());
            userInfo.put("email", editEmailField.getText().toString().trim());
            userInfo.put("password", editPasswordField.getText().toString().trim());

            db.child("userData").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).child("data").updateChildren(userInfo);

            fAuth.getCurrentUser().updateEmail(editEmailField.getText().toString()).addOnCompleteListener(task->{
                if(!task.isSuccessful()){
                    Toast.makeText(UserUpdate.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_LONG).show();
                }
            });

            fAuth.getCurrentUser().updatePassword(editPasswordField.getText().toString()).addOnCompleteListener(task->{
                if(!task.isSuccessful()){
                    Toast.makeText(UserUpdate.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_LONG).show();
                }
            });

            finish();
        });

        userCancelBtn.setOnClickListener(v->finish());


    }
}