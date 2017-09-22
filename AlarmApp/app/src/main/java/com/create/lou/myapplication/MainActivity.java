package com.create.lou.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v4.app.AlarmManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;


import org.w3c.dom.Text;

import java.sql.Time;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    //these are the different UI objects
    private Switch switchOn;
    private SeekBar seekBarAlrms, seekBarMins;
    private TimePicker timePicker;
    private TextView textValAlarm, textValMin, textAlarmIndex, textAlrmDiff;
    //private Button testButton, ackButton;

    //these are for saving the user data
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    //these are the variables used throughout the app
    private boolean pastAlarm, alarmOn;
    private int mHours, mMins, numAlarms, numMins;

    //making a calendar time so that the alarm can be set to a specific time
    final Calendar calendar = Calendar.getInstance();

    //don't know why this was added
    Context context;

    //used to get to the Alarm Receiver class
    PendingIntent pIntent;
    Intent mIntent;

    //used in conjunction with the intents to schedule getting to the Alarm Receiver class
    AlarmManager alarm_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set the file activity_main.xml to be the user interface
        setContentView(R.layout.activity_main);

        //don't know what this is here but
        this.context = this;

        //used to initialize all the variable in used in the interface
        initializeVariables();

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int min) {

                //updateTimeDisplay();
                updateCalendar();

            }
        });

        /*
        testButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mIntent.putExtra("Alarm State", true);
                sendBroadcast( mIntent);
            }
        });

        ackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntent.putExtra("Alarm State", false);
                sendBroadcast( mIntent);
            }
        });
        */

        //This is a listener for the main switch that turns on and off the alarms
        switchOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){


                //These two enabled methods modify the seekBars and only make them work if the main switch is on
                seekBarAlrms.setEnabled(isChecked);
                seekBarMins.setEnabled(isChecked);

                alarmOn = isChecked;

                //updateTimeDisplay();

                updateCalendar();

                mIntent.putExtra("Alarm State", isChecked);
                pIntent = PendingIntent.getBroadcast(MainActivity.this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //if the switch is on
                if(isChecked){

                    //updateTimeDisplay();

                    //Adding an extra boolean to give the current state of the switch so that you can immediantly turn off the alarm
                    //mIntent.putExtra("Alarm State", true);

                    //pIntent = PendingIntent.getBroadcast(MainActivity.this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarm_manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);

                    textAlarmIndex.setVisibility(View.VISIBLE);
                    textAlrmDiff.setVisibility(View.VISIBLE);

                }
                else{

                    //updateTimeDisplay();

                    textAlarmIndex.setVisibility(View.INVISIBLE);
                    textAlrmDiff.setVisibility(View.INVISIBLE);

                    //mIntent.putExtra("Alarm State", false);

                    //Immediantly sends signal to the alarm receiver to ask to stop the belltone
                    sendBroadcast(mIntent);

                    alarm_manager.cancel(pIntent);

                }
            }
        });

        //This is used to initialize the String value (if I don't have it it says 0 despite starting at 3)
        textValAlarm.setText("Alarms: " + numAlarms);
        textValMin.setText("Mins: " + numMins);

        //setting up the seekBars so that they can't be equal to 0
        seekBarAlrms.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int alrms = 0;                                                                  //value needs to be initialized in the listener or it crashes the program. this value can be
            @Override                                                                       //access outside the listener through seekbar.getProgress()
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {              //I don't want there to be 0 alarms because then it would not wake me up
                alrms = i + 1;
                numAlarms = alrms;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textValAlarm.setText("Alarms: " + alrms);
            }
        });
        seekBarMins.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int mins = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mins = i + 1;                                                           //I don't want there to be 0 mins between alarms because then there is just 1 alarm
                numMins = mins;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textValMin.setText("Mins: " + mins);
            }
        });

    }


    //This functions initializes all the variables
    private void initializeVariables(){

        //this is creating a file to store alarms -- file name is AlarmStorage...idk where it stores it
        sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        //bool for wether the alarm was left on or off - default is off
        alarmOn = sharedPref.getBoolean(getResources().getString(R.string.alarm_On), false);

        //switch in UI
        switchOn = (Switch) findViewById(R.id.switchOn);
        switchOn.setChecked(alarmOn);

        //seekbar for how many alarms - default is 3
        seekBarAlrms = (SeekBar) findViewById(R.id.seekBarAlrms);
        seekBarAlrms.setEnabled(alarmOn);
        seekBarAlrms.setProgress( sharedPref.getInt(getResources().getString(R.string.num_alrms), 3));

        //seekbar for how many minutes between alarms - default is 10
        seekBarMins = (SeekBar) findViewById(R.id.seekBarMins);
        seekBarMins.setEnabled(alarmOn);
        seekBarMins.setProgress( sharedPref.getInt(getResources().getString(R.string.num_Mins), 10));

        //timepicker should start at whatever value it was left at - default is 6:03
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setHour(sharedPref.getInt(getResources().getString(R.string.Alarm_Hour), 6));
        timePicker.setMinute(sharedPref.getInt(getResources().getString(R.string.Alarm_Min), 3));
        timePicker.setIs24HourView(true);

        //text view displays
        textValAlarm = (TextView) findViewById(R.id.textValNumAlrms);
        textValMin = (TextView) findViewById(R.id.textValSnoozeMins);
        textAlarmIndex = (TextView) findViewById(R.id.textAlarmInd);
        textAlrmDiff = (TextView) findViewById(R.id.textAlarmDiff);
        //testButton = (Button) findViewById(R.id.button);
        //ackButton = (Button) findViewById(R.id.buttonAck);

        //if the alarm is off make the Alarm time display invisible and the time till alarm
        if(!alarmOn) {
            textAlarmIndex.setVisibility(View.INVISIBLE);
            textAlrmDiff.setVisibility(View.INVISIBLE);
        } else{//else make them visible
            textAlarmIndex.setVisibility(View.VISIBLE);
            textAlrmDiff.setVisibility(View.VISIBLE);
        }

        //create the alarm manager
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //Setting up the intent to the AlarmReceiver Class
        mIntent = new Intent(this.context, AlarmReceiver.class);


    }

    //this function is for updating the display of the chosen alarm time
    private  void updateTimeDisplay(){

        long diffMillis, lDiffMins, lDiffHours;

        //this will be the amount of time until the alarm goes off
        diffMillis = calendar.getTimeInMillis() - System.currentTimeMillis();
        lDiffMins = diffMillis / (1000*60);
        if(lDiffMins >= 60) {
            lDiffHours = diffMillis / (1000*60*60);
            lDiffMins = lDiffMins%60;
        }
        else {
            lDiffHours = 0;
        }

        if(mMins < 10) {
            textAlarmIndex.setText("Alarm set at - " + mHours + ":0" + mMins);
        } else{
            textAlarmIndex.setText("Alarm set at - " + mHours + ":" + mMins);
        }
        textAlrmDiff.setText("Hours: " + lDiffHours + " Mins: " + lDiffMins);
    }

    private  void updateCalendar(){

        mMins = timePicker.getMinute();
        mHours = timePicker.getHour();
        numAlarms = seekBarAlrms.getProgress();
        numMins = seekBarMins.getProgress();

        calendar.set(Calendar.HOUR_OF_DAY, mHours );
        calendar.set(Calendar.MINUTE, mMins);

        if(pastAlarm){
            calendar.roll(Calendar.DAY_OF_YEAR, false);
            pastAlarm = false;
        }

        //if the calendar is set to a time that has already passed today change it to be for tomorrow
        if(System.currentTimeMillis() > calendar.getTimeInMillis()){
            calendar.roll(Calendar.DAY_OF_YEAR, true);
            pastAlarm = true;
        }

        updateTimeDisplay();
    }

    @Override
    public void onStop(){
        //store the shared Preferences stuff ie alarm hour, min, numbAlarms, numbMins
        editor.putInt( getResources().getString(R.string.Alarm_Hour) , mHours);
        editor.putInt( getResources().getString(R.string.Alarm_Min) , mMins);
        editor.putInt( getResources().getString(R.string.num_Alarm) , numAlarms);
        editor.putInt( getResources().getString(R.string.num_Mins) , numMins);
        editor.putBoolean( getResources().getString(R.string.alarm_On) , alarmOn);
        editor.apply();

        super.onStop();

    }


}
