package com.example.food4all.activities.volunteer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food4all.adapter.MyAdapter;
import com.example.food4all.modals.Fooddetails;
import com.example.food4all.R;
import com.example.food4all.utilities.ConstantValues;
import com.example.food4all.utilities.MyAppPrefsManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VolunteerActivity extends AppCompatActivity {
    public Button log;
    private FirebaseAuth firebaseAuth;
    private TextView userEmail;
    DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    RecyclerView recyclerView;
    ArrayList<Fooddetails> list;
    MyAdapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_openvolun);

        firebaseAuth = FirebaseAuth.getInstance();


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Food Details");
        }
        EditText editText = findViewById(R.id.edittext);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Food_Details");

        recyclerView = (RecyclerView) findViewById(R.id.myrecycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<Fooddetails>();


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Fooddetails f = dataSnapshot1.getValue(Fooddetails.class);
                    list.add(f);
                }

                myAdapter = new MyAdapter(VolunteerActivity.this, list);
                recyclerView.setAdapter(myAdapter);
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(VolunteerActivity.this, "Something is wrong !", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void filter(String text) {
        ArrayList<Fooddetails> filteredlist = new ArrayList<>();

        for (Fooddetails item : list) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }

        myAdapter.filterList(filteredlist);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            // Set UserLoggedIn in MyAppPrefsManager
            MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(VolunteerActivity.this);
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(VolunteerActivity.this, VolunteerLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            myAppPrefsManager.setUserLoggedIn(false);
            myAppPrefsManager.setUserName("");
            finish();
            return true;
        }

        if (item.getItemId() == R.id.action_gallery) {
            Intent gall = new Intent(VolunteerActivity.this, Upload_Photos.class);
            gall.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(gall);
            return true;
        }

        if (item.getItemId() == R.id.action_settings) {

            return true;
        }

        if (item.getItemId() == R.id.action_profile) {
            Intent prof = new Intent(VolunteerActivity.this, VolunteerProfile.class);
            prof.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(prof);
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


}
