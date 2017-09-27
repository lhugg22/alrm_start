package com.create.lou.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class SnoozeActivity extends AppCompatActivity {


    private Button snoozeBtn;
    private TextView textAlarmtime, textNumAlarms;

    Intent stopIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this is used so that the snooze shows up when the phone is locked
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_snooze);

        //this is the intent sent from the BellToneService w/ the extras
        final Intent dIntent = getIntent();

        //this function initializes the variables
        initializeVariables();

        //this provides visibility to how many alarms are left and time in between
        textAlarmtime.setText("Alarm In - " + dIntent.getExtras().getInt("Snooze Minutes") + " Minutes");
        textNumAlarms.setText("Number of Alarms = " + dIntent.getExtras().getInt("Number of Alarms"));

        snoozeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if the button is pressed send the turn off flag to the BellToneService
                //the other extras are passed just to be parallel
                stopIntent.putExtra("Alarm State", false);
                stopIntent.putExtra("Snooze Minutes", dIntent.getExtras().getInt("Snooze Minutes"));
                stopIntent.putExtra("Alarms Left: ", dIntent.getExtras().getInt("Number of Alarms"));
                //send the broadcast to stop playing the tone instantly
                sendBroadcast( stopIntent);

                //this passes the state of the alarm, minutes between alarms, and number of alarms to
                //the calendar function that will set up the snooze alarms
                updateCal(dIntent.getExtras().getBoolean("Alarm State"),
                        dIntent.getExtras().getInt("Snooze Minutes"),
                        dIntent.getExtras().getInt("Number of Alarms"));

                //once everything else is done, exit
                System.exit(0);

            }
        });

    }

    @Override
    public void onStop(){

        super.onStop();
    }

    //initializing the variables
    private void initializeVariables(){

        snoozeBtn = (Button) findViewById(R.id.buttonSnooze);

        stopIntent = new Intent(SnoozeActivity.this , AlarmReceiver.class);

        textAlarmtime = (TextView) findViewById(R.id.alarmTimeView);
        textNumAlarms = (TextView) findViewById(R.id.numAlarmsView);

    }

    //this function is used to make a new alarm at however many minutes after you snoozed the original alarm
    private void updateCal(boolean alarmState, int snzMins, int numAlrms){

        //if there are snoozes left
        if(numAlrms > 0){
            //make a new calendar
            final Calendar calCurrent = Calendar.getInstance();
            //addd the appropriate amount of time
            calCurrent.add(Calendar.MINUTE, snzMins);

            AlarmManager alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

            //make the intent with the appropriate extras and decrimenting the number of alarms
            Intent snoozeIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
            snoozeIntent.putExtra(getResources().getString(R.string.Ex_Alarm_State), alarmState);
            snoozeIntent.putExtra(getResources().getString(R.string.Ex_Snooze_Min), snzMins);
            snoozeIntent.putExtra(getResources().getString(R.string.Ex_Alarm_Num), numAlrms - 1);

            //make the intent into a pending intent
            PendingIntent psnzIntent = PendingIntent.getBroadcast(SnoozeActivity.this, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            //only turn on the alarm if the alarm switch is still in alarm position
            if(alarmState){
                alarm_manager.set(AlarmManager.RTC_WAKEUP, calCurrent.getTimeInMillis(), psnzIntent);
            }
        }

    }


}
