package com.tamaraoldham.smarttoastv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;


public class Operation extends AppCompatActivity {

    public String profileValue;
    Button EditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);

        //read out profile number
        Intent intentEdit = getIntent();
        profileValue = intentEdit.getStringExtra("PROFILE");

        //Edit profile
        EditProfile = findViewById(R.id.EditProfile);


    }
}
