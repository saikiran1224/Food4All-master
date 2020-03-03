package com.example.food4all.activities.recipients;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food4all.modals.Recipient_Modal;
import com.example.food4all.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Recipient_login extends AppCompatActivity {

    TextView login;
    EditText usname,pwd;
    Button submit;
    DatabaseReference databaseReference;
    String dbpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orphanage_login);

        login=(TextView)findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Recipient_login.this, RecipientRegistration.class);
                startActivity(intent);
            }
        });

        usname=(EditText)findViewById(R.id.usname);
        pwd=(EditText)findViewById(R.id.pwd);
        submit=(Button)findViewById(R.id.button);

        databaseReference= FirebaseDatabase.getInstance().getReference("Organization_Details");


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usnam = usname.getText().toString().trim();
             final String pw = pwd.getText().toString().trim();

                if(usnam.isEmpty()) {
                    usname.setError("Please enter Username");
                } else if(pw.isEmpty()) {
                    pwd.setError("Password should not be Empty");
                }
                else {
                    Query query = databaseReference.orderByChild("usname").equalTo(usnam);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {
                                // dataSnapshot is the "issue" node with all children with id 0
                                for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                    // do something with the individual "issues"
                                    dbpass=issue.getValue(Recipient_Modal.class).getPassword();
                                }
                                if(pw.equals(dbpass)) {
                                    Toast.makeText(Recipient_login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Recipient_login.this, RecipientActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(Recipient_login.this, "login Failed !", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();
        if (id == android.R.id.home)
        {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}