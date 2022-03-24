package com.example.adminmedico;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.adminmedico.Entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Register extends AppCompatActivity {
    EditText mFullName, mEmail, mPassword,mPasswordTwo;
    Button mRegisterButton;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    User user;
    DatabaseReference db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Objects.requireNonNull(getSupportActionBar()).hide();

        db = FirebaseDatabase.getInstance().getReference();
        mFullName = findViewById(R.id.fullName);
        mEmail = findViewById(R.id.mEmail);
        mPassword = findViewById(R.id.mPassword);
        mPasswordTwo = findViewById(R.id.passwordTwo);

        mRegisterButton = findViewById(R.id.regBtn);
        mLoginBtn = findViewById(R.id.logBtn);

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);



        mRegisterButton.setOnClickListener(v -> {
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            String passwordTwo = mPasswordTwo.getText().toString().trim();

            if(TextUtils.isEmpty(email))
            {
                mEmail. setError("Email is required");
                return;
            }

            if(TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordTwo))
            {
                mPassword.setError("Password is required");
                return;
            }

            if(password.length() < 6)
            {
                mPassword.setError("Password must be 6 characters or longer");
            }



            if(!password.equals(passwordTwo))
            {
                mPassword.setError("Password mismatch");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            //register the user in the database

            fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    String userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                    user = new User(userId, mFullName.getText().toString(), email, password);
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("userName", mFullName.getText().toString());
                    userData.put("email", email);
                    userData.put("password", password);

                    db.child("userData").child(userId).child("data").setValue(userData);

                    Toast.makeText(Register.this,"User successfully created",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }
                else
                {
                    Toast.makeText(Register.this, "Error !" + Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        });
    }

    public void goToLogin(View view) {
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }
}