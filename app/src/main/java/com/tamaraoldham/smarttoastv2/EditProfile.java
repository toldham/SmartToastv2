package com.tamaraoldham.smarttoastv2;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


//This edit page is for when you are adding a new profile, not editing a current profile

public class EditProfile extends AppCompatActivity {

    SharedPreferences sharedp1; //profile 1 shared preferences
    SharedPreferences sharedp2; //profile 2 shared preferences
    SharedPreferences sharedp3; //profile 3 shared preferences

    EditText profileName;
    Button save;
    //add in cook time variable

    //private int cookTime;
    private String name;
    public String profileValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent intentEdit = getIntent();
        profileValue = intentEdit.getStringExtra("PROFILE");

        profileName = findViewById(R.id.profileName);
        save = findViewById(R.id.Save);

        sharedp1 = getSharedPreferences("profile1", Context.MODE_PRIVATE);
        sharedp2 = getSharedPreferences("profile2", Context.MODE_PRIVATE);
        sharedp3 = getSharedPreferences("profile3", Context.MODE_PRIVATE);

        //Create editor to edit profile 1
        final Editor editorp1 = sharedp1.edit();
        final Editor editorp2 = sharedp2.edit();
        final Editor editorp3 = sharedp3.edit();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //read in values from name and cook time
                name = profileName.getText().toString();
                //define new intent to return to main menu
                Intent intent = new Intent(EditProfile.this, MainMenu.class);

                if (profileValue.equals("profile1")){
                    //save new info in profile1 and return to main menu
                    editorp1.putString("name",name);
                   //editorp1.putint("cooktime", cookTime);
                    editorp1.apply(); //apply() vs commit()

                    startActivity(intent);
                }
                else if (profileValue.equals("profile2")){
                    //save new info in profile2 and return to main menu
                    editorp2.putString("name",name);
                    //editorp2.putint("cooktime", cookTime);
                    editorp2.apply(); //apply() vs commit()

                    startActivity(intent);
                }
                else {
                    //save new info in profile3 and return to main menu
                    editorp3.putString("name",name);
                    //editorp3.putint("cooktime", cookTime);
                    editorp3.apply(); //apply() vs commit()

                    startActivity(intent);
                }

        }});





    }





}

