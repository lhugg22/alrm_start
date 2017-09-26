package com.create.lou.myapplication;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
//import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Calendar;

public class SnoozeActivity extends AppCompatActivity {


    private Button snoozeBtn;
    private int snzCnt;

    Intent stopIntent;//, snoozeIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_snooze);

        final Intent dIntent = getIntent();


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

                updateCal(dIntent.getExtras().getInt("Snooze Minutes"), dIntent.getExtras().getInt("Number of Alarms"));

                //send a bool flag to the main activity so that it knows the alarm has been snoozed
                //snoozeIntent.putExtra("Snz State", snzCnt);
                //startActivity(snoozeIntent);

                System.exit(0);

            }
        });

        //MainActivity mAct = new MainActivity();
        //mAct.updateCalendar();
    }

    @Override
    public void onStop(){
        super.onStop();



    }

    private void initializeVariables(){

        snoozeBtn = (Button) findViewById(R.id.buttonSnooze);

        stopIntent = new Intent(SnoozeActivity.this , AlarmReceiver.class);
        //snoozeIntent = new Intent(SnoozeActivity.this, MainActivity.class);

    }

    private void updateCal(int numAlrms, int snzMins){
        snzCnt++;

        MainActivity mAct = new MainActivity();

        if(snzCnt < numAlrms ){

            final Calendar calCurrent = Calendar.getInstance();
            calCurrent.add(Calendar.MINUTE, snzMins);

            mAct.updateCalendar(calCurrent.get(Calendar.HOUR_OF_DAY), calCurrent.get(Calendar.MINUTE));
        }

    }


}
