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

import com.example.adminmedico.Adapters.ContactAdapter;
import com.example.adminmedico.AddContact;
import com.example.adminmedico.ContactActivity;
import com.example.adminmedico.Entities.Contact;
import com.example.adminmedico.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ContactsFragment extends Fragment {

    ContactAdapter contactAdapter;
    RecyclerView recyclerViewContacts;
    ArrayList<Contact> contactList;
    FloatingActionButton btnAddContact;
    FirebaseAuth fAuth;
    DatabaseReference db;
    TextView emptyListContactTv;

    private Context mContext;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts,container,false);
        recyclerViewContacts = view.findViewById(R.id.recyclerViewContacts);
        contactList = new ArrayList<>();
        btnAddContact = view.findViewById(R.id.btnAddContact);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        emptyListContactTv = view.findViewById(R.id.tv_empty_list_contacts);

        //load list
        loadList();

        btnAddContact.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), AddContact.class);
            startActivity(i);
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadList();
    }

    public void loadList(){
        db.child("userData").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).child("contacts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactList.clear();
                for(DataSnapshot objSnapshot: snapshot.getChildren()){
                    contactList.add(new Contact(
                            Objects.requireNonNull(objSnapshot.child("name").getValue()).toString(),
                            Objects.requireNonNull(objSnapshot.child("number").getValue()).toString()));
                }
                if(contactList.size() > 0){
                    emptyListContactTv.setText("");
                }else{
                    emptyListContactTv.setText("No registered contacts");
                }
                showData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showData(){
        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
        contactAdapter = new ContactAdapter(mContext,contactList);
        recyclerViewContacts.setAdapter(contactAdapter);

        contactAdapter.setOnclickListener(v -> {
            Intent i = new Intent(mContext, ContactActivity.class);
            i.putExtra("contactNumber", contactList.get(recyclerViewContacts.getChildAdapterPosition(v)).getNumber());
            startActivity(i);
        });
    }
}
