package com.gmrit.food4all.activities.donor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gmrit.food4all.OTP_ValidationActivity;
import com.gmrit.food4all.R;
import com.gmrit.food4all.activities.volunteer.VolunteerRegistrationActivity;
import com.gmrit.food4all.modals.Fooddetails;
import com.gmrit.food4all.utilities.ConstantValues;
import com.gmrit.food4all.utilities.Dialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FoodDetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public Button submit;
    private FirebaseAuth firebaseAuth;
    private TextView userEmail;
    DatabaseReference reff;
    DatabaseReference ref;
    EditText edtnam, edtphone, spin, edtadd, edtfoodcanfeed;
    Fooddetails fooddetails;
    boolean connected = false;
    FirebaseAuth mAuth;
    String name, place, address, phon, foodno, tim, currdate, codesent;
    /*Geocoder geocoder;
    List<Address> addresses;
    LocationTrack locationTrack;
    String location_address,lat,lon,knownName,city,postalCode;*/

    private static String TAG = "TOKENS_DATA";

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + ConstantValues.AUTH_KEY_FCM;
    final private String contentType = "application/json";
    String TOPIC;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food__details);
        firebaseAuth = FirebaseAuth.getInstance();
        submit = (Button) findViewById(R.id.sub);
        edtnam = (EditText) findViewById(R.id.name);
        edtphone = (EditText) findViewById(R.id.phone);
        edtadd = (EditText) findViewById(R.id.add);
        edtfoodcanfeed = (EditText) findViewById(R.id.noofpeople);

        mAuth = FirebaseAuth.getInstance();

        /*locationTrack = new LocationTrack(this);

        lat = Double.toString(locationTrack.getLatitude());
        lon = Double.toString(locationTrack.getLongitude());

        geocoder = new Geocoder(this, Locale.getDefault());
*/

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Donate Food");
        }


        ref = (DatabaseReference) FirebaseDatabase.getInstance().getReference("Volunteers");

        reff = FirebaseDatabase.getInstance().getReference().child("Food_Details");

        FirebaseMessaging.getInstance().subscribeToTopic("/topics/userABC1");


        final Spinner spinner = findViewById(R.id.spin);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.placetype, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        final Spinner time = findViewById(R.id.time);
        ArrayAdapter<CharSequence> adap = ArrayAdapter.createFromResource(this, R.array.timetaken, android.R.layout.simple_spinner_dropdown_item);
        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        time.setAdapter(adap);
        time.setOnItemSelectedListener(this);

        /*edtadd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (edtadd.getRight() - edtadd.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        try {
                            if(lat.equals("0.0") || lon.equals("0.0"))
                                locationTrack.showSettingsAlert();
                            else {
                                //Toast.makeText(SthreeRaksha.this, "Latitude " + lat + "\n Longitude " + lon, Toast.LENGTH_SHORT).show();
                                Log.d("Latitude", lat);
                                Log.d("Longitude", lon);
                                addresses = geocoder.getFromLocation(Double.parseDouble(lat),Double.parseDouble(lon),1);
*//*
                                knownName = addresses.get(0).getFeatureName();
                                city = addresses.get(0).getLocality();
                                postalCode = addresses.get(0).getPostalCode();*//*

                                location_address = addresses.get(0).getAddressLine(0);

                                //location_address = knownName + ", " +city + ", " +postalCode; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                Log.d("address",location_address);
                                edtadd.setText(location_address);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(FoodDetailsActivity.this, "Location set Successfully !", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        });*/


        FirebaseMessaging.getInstance().subscribeToTopic("/topics/userABC1");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                place = spinner.getSelectedItem().toString();
                name = edtnam.getText().toString().trim();
                phon = edtphone.getText().toString().trim();
                address = edtadd.getText().toString().trim();
                tim = time.getSelectedItem().toString();
                foodno = edtfoodcanfeed.getText().toString().trim();

                // Toast.makeText(FoodDetailsActivity.this, ""+phon, Toast.LENGTH_SHORT).show();

                Date cd = Calendar.getInstance().getTime();
                System.out.println("Current time => " + cd);
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                currdate = df.format(cd);

                if (name.isEmpty()) {
                    Toast.makeText(FoodDetailsActivity.this, "Please enter Name", Toast.LENGTH_LONG).show();
                } else if (phon.isEmpty()) {
                    Toast.makeText(FoodDetailsActivity.this, "Please enter Phone Number", Toast.LENGTH_LONG).show();
                } else if (phon.length() < 10) {
                    Toast.makeText(FoodDetailsActivity.this, "Phone Number is Invalid", Toast.LENGTH_SHORT).show();
                } else if (address.isEmpty()) {
                    Toast.makeText(FoodDetailsActivity.this, "Please enter Address", Toast.LENGTH_LONG).show();
                } else if (foodno.isEmpty()) {
                    Toast.makeText(FoodDetailsActivity.this, "Please enter Food can feed Number", Toast.LENGTH_SHORT).show();
                } else if (foodno.equals("0")) {
                    Toast.makeText(FoodDetailsActivity.this, "Please enter Number greater than 1", Toast.LENGTH_SHORT).show();
                } else if (place.equals("Choose Type of Place")) {
                    Toast.makeText(FoodDetailsActivity.this, "Please choose Type of Place", Toast.LENGTH_LONG).show();
                } else if (tim.equals("Cooked Before")) {
                    Toast.makeText(FoodDetailsActivity.this, "Please select Time of Period", Toast.LENGTH_LONG).show();
                } else {
                    sendVerificationCode("+91" + phon);
                    Toast.makeText(FoodDetailsActivity.this, "One Time Password has been sent to Your Mobile Number " + "+91" + phon, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void sendVerificationCode(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);
        Log.d(TAG, "sendVerificationCode: " + phoneNumber);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted: " + phoneAuthCredential);

            reff.push().setValue(new Fooddetails(name, phon, address, place, "", tim, currdate, foodno));


            try {

                RequestQueue queue = Volley.newRequestQueue(FoodDetailsActivity.this);

                String url = "https://fcm.googleapis.com/fcm/send";

                String m = "Mr./Mrs." + name + " is willing to Donate Food from a ";
                String num = m + place + " which can be fed to " + foodno + " person(s)";
                String res = num + "\nAddress is " + address;
                String add = res + "\nFood Cooked Before :" + tim;
                String fina = add + "\nContact: " + phon;

                TOPIC = "/topics/donateFood";
                JSONObject data = new JSONObject();
                data.put("title", "Food Ready to Donate");
                data.put("message", fina);
                Log.e(TAG, "" + data);

                JSONObject notification_data = new JSONObject();
                notification_data.put("data", data);
                notification_data.put("to", TOPIC);

                Log.e(TAG, "" + notification_data);


                JsonObjectRequest request = new JsonObjectRequest(url, notification_data, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {

                        Map<String, String> params = new HashMap<>();
                        params.put("Authorization", serverKey);
                        params.put("Content-Type", contentType);
                        return params;
                    }
                };

                queue.add(request);

            } catch (Exception e) {
                e.printStackTrace();
            }

            Toast.makeText(FoodDetailsActivity.this, "Food Donation details Submitted Successfully !", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(OTP_ValidationActivity.this, MainActivity.class));
            openDialog();

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.d(TAG, "onVerificationFailed: " + e.toString());
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // [START_EXCLUDE]
                Toast.makeText(FoodDetailsActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // [START_EXCLUDE]
                /*Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                        Snackbar.LENGTH_SHORT).show();*/
                Toast.makeText(FoodDetailsActivity.this, "SMS Quota Exceeded", Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            } else {
                Toast.makeText(FoodDetailsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codesent = s;
            Log.d(TAG, "onCodeSent: " + s);
            goToNext();
        }
    };

    public void goToNext() {
        Intent intent = new Intent(FoodDetailsActivity.this, OTP_ValidationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("phone", phon);
        bundle.putString("address", address);
        bundle.putString("place", place);
        bundle.putString("foodno", foodno);
        bundle.putString("time", tim);
        bundle.putString("date", currdate);
        bundle.putString("code", codesent);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void openDialog() {
        Dialog d = new Dialog();
        d.show(getSupportFragmentManager(), "Dialog");
    }

}