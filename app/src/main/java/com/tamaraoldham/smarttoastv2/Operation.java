package com.tamaraoldham.smarttoastv2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
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

//Operation Screen where it loads profile information
//Option to edit or delete
//Cook Time and Delay Time labels are static

public class Operation extends AppCompatActivity {

    public String profileValue;
    Button EditProfile;
    Button startOperation;
    TextView DisplayProfileName;
    TextView DelayTimer;
    TextView CookTime;
    int cookTimer;
    int mins;
    int seconds;
    int cookMins;
    int cookSeconds;
    int cookMultiplier;
    int offsetMultiplier;
    int offset;
    int cookTimerSeconds;
    int offsetSeconds;
    Button AddDelay;
    Button delete;
    int delaytime = 0;
    String delay;
    String cook;
    String profile;
    boolean timeRunning = false;
    String CANCEL = "CANCEL";
    String START = "START";
    long delayMillisecondsLeft;
    long cookMillisecondsLeft;
    String delaySend;
    boolean delayStatus = false;
    boolean cookStatus = false;

    SharedPreferences sharedp1;
    SharedPreferences sharedp2;
    SharedPreferences sharedp3;

    SharedPreferences.Editor editorp1;
    SharedPreferences.Editor editorp2;
    SharedPreferences.Editor editorp3;


    private CountDownTimer delayCountDownTimer;
    private CountDownTimer cookCountDownTimer;

    String server_url = "http://192.168.1.90/";
    TextView response; //error message for ESP8266

    boolean isCancelled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);

        //read out profile number
        Intent intentEdit = getIntent();
        profileValue = intentEdit.getStringExtra("PROFILE");

        sharedp1 = getSharedPreferences("profile1", Context.MODE_PRIVATE);
        sharedp2 = getSharedPreferences("profile2", Context.MODE_PRIVATE);
        sharedp3 = getSharedPreferences("profile3", Context.MODE_PRIVATE);

        //Create editor to edit profile 1
        editorp1 = sharedp1.edit();
        editorp2 = sharedp2.edit();
        editorp3 = sharedp3.edit();

        DisplayProfileName = findViewById(R.id.DisplayProfileName);
        DelayTimer = findViewById(R.id.DelayTimer);
        CookTime = findViewById(R.id.CookTimer);

        //Edit profile
        EditProfile = findViewById(R.id.EditProfile);
        //Start Button
        startOperation = findViewById(R.id.startButton);
        //Add Delay Button
        AddDelay = findViewById(R.id.AddDelay);
        //error response for ESP8266
        response = findViewById(R.id.response);
        //delete button
        delete = findViewById(R.id.delete);

        cookMultiplier = 30; //30 seconds
        offsetMultiplier = 15; //15 seconds

        //load profile contents to display, i.e. cook timer, name
        display();
        setCookTimer();

        //add delay time, add 30 seconds per delay time
        AddDelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add 30 seconds to the previous delay time
                delaytime = delaytime + 30;
                setDelayTimer();
            }
        });

        startOperation.setOnClickListener(new View.OnClickListener() { //START or CANCEL
            @Override
            public void onClick(View v) {

                startCancel();

            }
        });

        EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Operation.this, EditProfile2.class);
                intent.putExtra("PROFILE", profileValue);
                startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
                Intent intent = new Intent(Operation.this, MainMenu.class);
                startActivity(intent);
            }
        });
    }
        //need to add in offset to cook time....
        public void display() {
            if (profileValue.equals("profile1")){
                //display profile1 information
                DisplayProfileName.setText(String.valueOf(sharedp1.getString("name", "Add Profile 1")));
                //COOK TIME integer value
                cookTimer = sharedp1.getInt("cooktime",0);
                //offset integer value
                offset = sharedp1.getInt("offset",0);
                //convert to seconds
                cookTimerSeconds = cookTimer * cookMultiplier;
                offsetSeconds = offsetMultiplier * offset;
                cookTimerSeconds = cookTimerSeconds + offset;
            }

            else if (profileValue.equals("profile2")){
                //display profile1 information
                DisplayProfileName.setText(String.valueOf(sharedp2.getString("name", "Add Profile 2")));
                //COOK TIME integer value
                cookTimer = sharedp2.getInt("cooktime",0);
                //offset integer value
                offset = sharedp2.getInt("offset", 0);
                //convert to seconds
                cookTimerSeconds = cookTimer * cookMultiplier;
                offsetSeconds = offsetMultiplier * offset;
                cookTimerSeconds = cookTimerSeconds + offset;
            }
            else {
                //display profile1 information
                DisplayProfileName.setText(String.valueOf(sharedp3.getString("name", "Add Profile 3")));
                //COOK TIME integer value
                cookTimer = sharedp3.getInt("cooktime",0);
                //offset integer value
                offset = sharedp3.getInt("offset",0);
                //convert to seconds
                cookTimerSeconds = cookTimer * cookMultiplier;
                offsetSeconds = offsetMultiplier * offset;
                cookTimerSeconds = cookTimerSeconds + offset;
            }
        }

        public void setCookTimer(){
            //math to determine seconds and minutes using cook timer variable (give cook time in seconds)
            //display time on timer
            cookMins = (cookTimerSeconds + offsetSeconds) / 60;
            cookSeconds = (cookTimerSeconds + offsetSeconds) % 60;
            if (cookSeconds < 10){
                cook = cookMins + ":0" + cookSeconds;
            }
            else{
                cook = cookMins + ":" + cookSeconds;
            }
            CookTime.setText(cook);
        }

        public void setDelayTimer(){
            mins = delaytime / 60;
            seconds = delaytime % 60;
            if (seconds < 10){
                delay = mins + ":0" + seconds;
            }
            else{
                delay = mins + ":" + seconds;
            }
            DelayTimer.setText(delay);
        }

        public void startCancel(){ //determine if START or CANCEL is being pressed
            if (!timeRunning){
                //send profile information over to MCU **NEED TO ADD VOLLEY HERE
                startOperation.setText(CANCEL); //set button to display cancel
                //disable other buttons
                AddDelay.setEnabled(false);
                EditProfile.setEnabled(false);
                delete.setEnabled(false);
                startTimers();
                timeRunning = true;
            }
            else{
                //send cancel information over to MCU
                sendCancel();
                isCancelled = true;

            }

        }

        public void startTimers(){ //used to determine which timer to start, delay or cook
            if (delaytime > 0){
                delayStatus = true;
                sendProfile();
                //startDelay();
            }
            else{
                cookStatus = true;
                sendProfile();
                //startCook();
            }

        }

        public void startDelay(){ //starts and updates delay timer
            //convert delaytime to long
            delayMillisecondsLeft = (Long.valueOf(delaytime))*1000; //convert seconds to ms
            delayCountDownTimer = new CountDownTimer(delayMillisecondsLeft,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    delayMillisecondsLeft = millisUntilFinished;
                    updateDelayTimer();

                    if (isCancelled){
                        cancel();
                        startOperation.setText(START); //set button to display start
                        Intent intent = new Intent(Operation.this, MainMenu.class);
                        startActivity(intent);

                    }
                }

                @Override
                public void onFinish() { //start cook timer when delay timer is done
                    startCook();
                }
            }.start();

        }

        public void updateDelayTimer(){
            mins = (int) delayMillisecondsLeft / 60000;
            seconds = (int) delayMillisecondsLeft % 60000 / 1000;
            if (seconds < 10){
                delay = mins + ":0" + seconds;
            }
            else{
                delay = mins + ":" + seconds;
            }
            DelayTimer.setText(delay);
        }

        public void startCook(){ //starts and updates cook timer
            //convert cooktime to long
            cookMillisecondsLeft = (Long.valueOf(cookTimerSeconds)) * 1000;
            cookCountDownTimer = new CountDownTimer(cookMillisecondsLeft, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    cookMillisecondsLeft = millisUntilFinished;
                    updateCookTimer();

                    if (isCancelled){
                        cancel();
                        startOperation.setText(START);
                        Intent intent = new Intent(Operation.this, MainMenu.class);
                        startActivity(intent);

                    }
                }

                @Override
                public void onFinish() {
                    Intent intent = new Intent(Operation.this, MainMenu.class);
                    startActivity(intent);
                }
            }.start();

        }

        public void updateCookTimer(){
            cookMins = (int) cookMillisecondsLeft / 60000;
            cookSeconds = (int) cookMillisecondsLeft % 60000 / 1000;
            if (cookSeconds < 10){
                cook = cookMins + ":0" + cookSeconds;
            }
            else{
                cook = cookMins + ":" + cookSeconds;
            }
            CookTime.setText(cook);
        }

        public void sendProfile(){
            final RequestQueue requestQueue = Volley.newRequestQueue(Operation.this);
            //need to send 3 delay, cooktime number, offset number "123456X"
            convertDelay();
            profile = server_url + "profile?profile=" + "1" + offset + delaytime + cookTimer + "x";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, profile, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (delayStatus){
                        startDelay();
                    }
                    if (cookStatus) {
                        startCook();
                    }
                    requestQueue.stop();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    response.setText("ESP8266 ERROR");
                    error.printStackTrace();
                    requestQueue.stop();
                    //consider restarting activity for future...
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Operation.this.recreate();
                        }
                    }, 5000);
                }
            });
            requestQueue.add(stringRequest);
        }

        public void sendCancel(){
            final RequestQueue requestQueue = Volley.newRequestQueue(Operation.this);
            profile = server_url + "cancel";

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
                    //consider restarting activity for future...
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Operation.this.recreate();
                        }
                    }, 5000);
                }
            });
            requestQueue.add(stringRequest);
        }

        public void convertDelay() { //convert delay to 3 digits to send to MCU
            if (delaytime < 10) {
                delaySend = "00" + delaytime;
            }
            if (delaytime < 100) {
                delaySend = "0" + delaytime;
            }
        }

        public void delete() {
            switch (profileValue) {
                case "profile1":
                    editorp1.putString("name", "Add Profile 1");
                    editorp1.remove("cooktime");
                    editorp1.remove("offset");
                    editorp1.apply();
                    break;
                case "profile2":
                    editorp2.putString("name", "Add Profile 2");
                    editorp2.remove("cooktime");
                    editorp2.remove("offset");
                    editorp2.apply();
                    break;
                case "profile3":
                    editorp3.putString("name", "Add Profile 3");
                    editorp3.remove("cooktime");
                    editorp3.remove("offset");
                    editorp3.apply();
                    break;

            }
        }


}

