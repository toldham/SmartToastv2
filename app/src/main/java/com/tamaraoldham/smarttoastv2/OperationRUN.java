package com.tamaraoldham.smarttoastv2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class OperationRUN extends AppCompatActivity {

    String profileValue;
    Button keepWarmButton;
    Button raiseToastButton;
    String profile;
    String server_url2 = "http://192.168.1.87/";
    //String server_url2 = "http://192.168.43.197/";
    TextView response; //error message for ESP8266

    SharedPreferences sharedp1;
    SharedPreferences sharedp2;
    SharedPreferences sharedp3;

    SharedPreferences.Editor editorp1;
    SharedPreferences.Editor editorp2;
    SharedPreferences.Editor editorp3;

    int feedbackCounter;
    int feedbackCounter1;
    int feedbackCounter2;
    int feedbackCounter3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_run);

        //pull out profile value -> used to determine if feedback is required
        Intent intent = getIntent();
        profileValue = intent.getStringExtra("PROFILE");

        sharedp1 = getSharedPreferences("profile1", Context.MODE_PRIVATE);
        sharedp2 = getSharedPreferences("profile2", Context.MODE_PRIVATE);
        sharedp3 = getSharedPreferences("profile3", Context.MODE_PRIVATE);

        editorp1 = sharedp1.edit();
        editorp2 = sharedp2.edit();
        editorp3 = sharedp3.edit();

        feedbackCounter1 = sharedp1.getInt("feedback", 0);
        feedbackCounter2 = sharedp2.getInt("feedback", 0);
        feedbackCounter3 = sharedp3.getInt("feedback", 0);

        //define "keep warm" and "raise toast"
        keepWarmButton = findViewById(R.id.keepWarmButton);
        raiseToastButton = findViewById(R.id.raiseToastButton);

        keepWarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send request to MCU -> can keep warm for as long as you want
                sendKeepWarmRequest();
            }
        });

        raiseToastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRaiseToastRequest();

                    Intent intent = new Intent(OperationRUN.this, FeedBack.class);
                    intent.putExtra("PROFILE", profileValue);
                    startActivity(intent);
            }
        });
    }

    public void sendKeepWarmRequest(){
            final RequestQueue requestQueue = Volley.newRequestQueue(OperationRUN.this);
            profile = server_url2 + "profile?profile=" + "3" + "00000" + "x";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, profile, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    keepWarmButton.setEnabled(false);
                    requestQueue.stop();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    response.setText("ESP8266 ERROR");
                    error.printStackTrace();
                    requestQueue.stop();

                }
            });
            requestQueue.add(stringRequest);
    }

    public void sendRaiseToastRequest(){
            final RequestQueue requestQueue = Volley.newRequestQueue(OperationRUN.this);
            profile = server_url2 + "profile?profile=" + "4" + "00000" + "x";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, profile, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    requestQueue.stop();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    response.setText("ESP8266 ERROR");
                    error.printStackTrace();
                    requestQueue.stop();

                }
            });
            requestQueue.add(stringRequest);
    }

    public void checkFeedback(){
        //new intent
        switch (profileValue){
            case ("profile1"):
                feedbackCounter = sharedp1.getInt("feedback", 1);
            case ("profile2"):
                feedbackCounter = sharedp2.getInt("feedback",1);
            case ("profile3"):
                feedbackCounter = sharedp3.getInt("feedback",1);
        }

    }


}
