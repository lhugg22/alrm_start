package com.create.lou.myapplication;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class SnoozeActivity extends AppCompatActivity {


    private Button snoozeBtn;

    Intent stopIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_snooze);


        //final WallpaperManager wpManager = WallpaperManager.getInstance(this);
        //final Drawable wpDrawable = wpManager.getDrawable();

        //LinearLayoutCompat ll = (LinearLayoutCompat) findViewById(R.layout.activity_snooze);
        //ll.setBackground(wpDrawable);

        initializeVariables();

        snoozeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stopIntent.putExtra("Alarm State", false);
                sendBroadcast( stopIntent);

                //send a bool flag to the main activity so that it knows the alarm has been snoozed

                System.exit(0);

            }
        });

        MainActivity mAct = new MainActivity();
        mAct.calendar.add(Calendar.MINUTE, x);

    }

    @Override
    public void onStop(){
        super.onStop();



    }

    private void initializeVariables(){

        snoozeBtn = (Button) findViewById(R.id.buttonSnooze);

        stopIntent = new Intent(SnoozeActivity.this , AlarmReceiver.class);

    }


}
