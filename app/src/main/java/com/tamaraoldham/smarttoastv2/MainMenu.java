package com.tamaraoldham.smarttoastv2;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.content.SharedPreferences;

//Shared Preferences will contain keys: "name", "cooktime", "offset")

public class MainMenu extends AppCompatActivity {


    Button profile1Button;
    Button profile2Button;
    Button profile3Button;

    SharedPreferences sharedp1;
    SharedPreferences sharedp2;
    SharedPreferences sharedp3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        profile1Button = findViewById(R.id.profile1Button);
        profile2Button = findViewById(R.id.profile2Button);
        profile3Button = findViewById(R.id.profile3Button);


        sharedp1 = getSharedPreferences("profile1", Context.MODE_PRIVATE);
        sharedp2 = getSharedPreferences("profile2", Context.MODE_PRIVATE);
        sharedp3 = getSharedPreferences("profile3", Context.MODE_PRIVATE);

       if (sharedp1.contains("name")){
            //if profile1 has been made, set button to that name, otherwise keep default value
          profile1Button.setText(String.valueOf(sharedp1.getString("name", "Add Profile 1")));
       }

        if (sharedp2.contains("name")){
            //if profile2 has been made, set button to that name, otherwise keep default value
            profile2Button.setText(String.valueOf(sharedp2.getString("name", "Add Profile 2")));
        }

        if (sharedp3.contains("name")){
            //if profile3 has been made, set button to that name, otherwise keep default value
            profile3Button.setText(String.valueOf(sharedp3.getString("name", "Add Profile 3")));
        }

    }

    public void SelectProfile1(View view) {
        //if profile1 has not been created yet, go to edit screen, pass through "profile1 tag"
        if ((profile1Button.getText().toString()).equals("Add Profile 1")) {
            Intent intent = new Intent(MainMenu.this, EditProfile.class);
            //add in variable to pass through
            intent.putExtra("PROFILE", "profile1");
            startActivity(intent);
        }
        //otherwise go to operation screen, pass through "profile1 tag"
    }

    public void SelectProfile2(View view) {
        //if profile2 has not been created yet, go to edit screen, pass through "profile2 tag"
        if ((profile2Button.getText().toString()).equals("Add Profile 2")) {
            Intent intent = new Intent(MainMenu.this, EditProfile.class);
            //add in variable to pass through
            intent.putExtra("PROFILE", "profile2");
            startActivity(intent);
        }
        //otherwise go to operation screen, pass through "profile1 tag"
    }

    public void SelectProfile3(View view) {
        //if profile3 has not been created yet, go to edit screen, pass through "profile3 tag"
        if ((profile3Button.getText().toString()).equals("Add Profile 3")) {
            Intent intent = new Intent(MainMenu.this, EditProfile.class);
            //add in variable to pass through
            intent.putExtra("PROFILE", "profile3");
            startActivity(intent);
        }
        //otherwise go to operation screen, pass through "profile1 tag"
    }

    //repeat for profile2 and profile3
}
