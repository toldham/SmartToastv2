package com.tamaraoldham.smarttoastv2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    Button AddDelay;
    int delaytime = 0;
    String delay;
    String cook;
    String profile;
    boolean timeRunning = false;
    String CANCEL = "CANCEL";
    String START = "START";
    long delayMillisecondsLeft;
    long cookMillisecondsLeft;

    SharedPreferences sharedp1;
    SharedPreferences sharedp2;
    SharedPreferences sharedp3;

    private CountDownTimer delayCountDownTimer;
    private CountDownTimer cookCountDownTimer;


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

        DisplayProfileName = findViewById(R.id.DisplayProfileName);
        DelayTimer = findViewById(R.id.DelayTimer);
        CookTime = findViewById(R.id.CookTimer);

        //Edit profile
        EditProfile = findViewById(R.id.EditProfile);
        //Start Button
        startOperation = findViewById(R.id.startButton);
        //Add Delay Button
        AddDelay = findViewById(R.id.AddDelay);

        cookTimer = 30; //test value

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

                //test concatenating string information that will be sent to ESP
                //code 1 = profile; code + cooktime + delaytime + offset

                //profile = "1" + delaytime;//use this for volley....
                //Use volley to send "profile" to webserver

                //Intent intentStart = new Intent(Operation.this, OperationRUN.class);
                //Bundle extras = new Bundle();

                //send over delay time and profile number
                //extras.putInt("DELAY_TIME", delaytime);
                //extras.putString("PROFILE", profileValue);
                //intentStart.putExtras(extras);
                //startActivity(intentStart);

                startCancel();

            }
        });

        EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Operation.this, EditProfile2.class);
                intent.putExtra("PROFILE","profile1");
                startActivity(intent);
            }
        });
    }
        //need to add in offset to cook time....
        public void display() {
            if (profileValue.equals("profile1")){
                //display profile1 information
                DisplayProfileName.setText(String.valueOf(sharedp1.getString("name", "Add Profile 1")));
                //COOK TIME
                //cookTimer = sharedp1.getInt("cooktime",0);
                //set minutes/seconds
            }

            else if (profileValue.equals("profile2")){
                //display profile1 information
                DisplayProfileName.setText(String.valueOf(sharedp2.getString("name", "Add Profile 2")));
                //COOK TIME
                //cookTimer = sharedp2.getInt("cooktime",0);
            }
            else {
                //display profile1 information
                DisplayProfileName.setText(String.valueOf(sharedp3.getString("name", "Add Profile 3")));
                //COOK TIME
                //cookTimer = sharedp3.getInt("cooktime",0);
            }
        }

        public void setCookTimer(){
            //math to determine seconds and minutes using cook timer variable (give cook time in seconds)
            //display time on timer
            cookMins = cookTimer / 60;
            cookSeconds = cookTimer % 60;
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
                startTimers();
                timeRunning = true;
            }
            else{
                //send cancel information over to MCU **NEED TO ADD VOLLEY HERE
                startOperation.setText(START); //set button to display start
                //re-enable other buttons
                //return to main menu
                Intent intent = new Intent(Operation.this, MainMenu.class);
                startActivity(intent);
            }

        }

        public void startTimers(){ //used to determine which timer to start, delay or cook
            if (delaytime > 0){
                startDelay();
            }
            else{
                startCook();
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
            cookMillisecondsLeft = (Long.valueOf(cookTimer)) * 1000;
            cookCountDownTimer = new CountDownTimer(cookMillisecondsLeft, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    cookMillisecondsLeft = millisUntilFinished;
                    updateCookTimer();
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

}

