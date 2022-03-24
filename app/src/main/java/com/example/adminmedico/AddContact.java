package com.example.adminmedico;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.adminmedico.Entities.Contact;
import com.example.adminmedico.Entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddContact extends AppCompatActivity {

    FirebaseAuth mAuth;

    DatabaseReference databaseReference;

    EditText nameField, phoneField;

    Button btnCreateContact, btnCancel;

    User user = new User();
    Contact contact = new Contact();
    String numberAux;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean editing = getIntent().getStringExtra("number") != null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Add Contact");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        btnCreateContact = findViewById(R.id.btnCreateContact);
        btnCancel = findViewById(R.id.btnCancel);

        nameField = findViewById(R.id.nameField);
        phoneField = findViewById(R.id.phoneField);

        user.setId(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

        if(editing){
            nameField.setText(getIntent().getStringExtra("name"));
            phoneField.setText(getIntent().getStringExtra("number"));
            btnCreateContact.setText("Save");
            numberAux = phoneField.getText().toString();
        }

        btnCreateContact.setOnClickListener(v -> {
            final Map<String,Object> contactInfo = new HashMap<>();


            if(TextUtils.isEmpty(nameField.getText().toString())){
                nameField.setError("Name is required");
                return;
            }
            if(TextUtils.isEmpty(phoneField.getText().toString())){
                phoneField.setError("Phone number is required");
                return;
            }
            if(!TextUtils.isDigitsOnly(phoneField.getText().toString()) || phoneField.getText().toString().length() != 10){
                phoneField.setError("Phone number must be valid");
                return;
            }

            contact.setName(nameField.getText().toString());
            contact.setNumber(phoneField.getText().toString());

            contactInfo.put("name", contact.getName());
            contactInfo.put("number", contact.getNumber());


            databaseReference.child("userData").child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if(!editing){
                            for(DataSnapshot objSnapshot : snapshot.child("contacts").getChildren()){
                                if(contact.getNumber().equals(Objects.requireNonNull(objSnapshot.getKey()))){
                                    phoneField.setError("Phone number already in use");
                                    return;
                                }
                            }
                        }

                        if(editing){
                            databaseReference.child("userData").child(mAuth.getCurrentUser().getUid()).child("contacts").child(numberAux).removeValue();
                        }

                        databaseReference.child("userData").child(user.getId()).child("contacts").child(contact.getNumber()).setValue(contactInfo).addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                String message = editing? "Contact successfully changed" : "Contact successfully created";
                                Toast.makeText(AddContact.this, message, Toast.LENGTH_LONG).show();
                                finish();
                            }else{
                                Toast.makeText(AddContact.this, "An error occurred", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });
        btnCancel.setOnClickListener(v -> finish());
    }

}