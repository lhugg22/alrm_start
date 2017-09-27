package com.create.lou.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    //these are the different UI objects
    private Switch switchOn;
    private SeekBar seekBarAlrms, seekBarMins;
    private TimePicker timePicker;
    private TextView textValAlarm, textValMin, textAlarmIndex, textAlrmDiff;

    //these are for saving the user data
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    //don't know why this was added
    Context context;

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

                //if the time is changed update the calendar so that we know what the current desired alarm time is
                updateCalendar( hour, min);

            }
        });


        //This is a listener for the main switch that turns on and off the alarms
        switchOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){

                //These two enabled methods modify the seekBars and only make them work if the main switch is on
                seekBarAlrms.setEnabled(isChecked);
                seekBarMins.setEnabled(isChecked);

                //if the switch is turned on set the alarm manager to the correct time
                updateCalendar(timePicker.getHour(), timePicker.getMinute());
            }
        });

        //This is used to initialize the String value
        textValAlarm.setText("Alarms: " + seekBarAlrms.getProgress());
        textValMin.setText("Mins: " + seekBarMins.getProgress());

        //this is used to pick how many times the alarm will go off
        seekBarAlrms.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {              //I don't want there to be 0 alarms because then it would not wake me up
                updateCalendar(timePicker.getHour(), timePicker.getMinute());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textValAlarm.setText("Alarms: " + (seekBar.getProgress() + 1));
            }
        });

        //this is used to pick how many minutes between alarms
        seekBarMins.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updateCalendar(timePicker.getHour(), timePicker.getMinute());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textValMin.setText("Mins: " + (seekBar.getProgress() + 1)); //mins);
            }
        });

    }


    //This functions initializes all the variables
    private void initializeVariables(){

        //used to store the saved user pref
        boolean alarmOn;

        //this is creating a file to store alarms -- file name is AlarmStorage...idk where it stores it
        sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        //bool for whether the alarm was left on or off - default is off
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

        //if the alarm is off make the Alarm time display invisible and the time till alarm
        if(!alarmOn) {
            textAlarmIndex.setVisibility(View.INVISIBLE);
            textAlrmDiff.setVisibility(View.INVISIBLE);
        } else{//else make them visible
            textAlarmIndex.setVisibility(View.VISIBLE);
            textAlrmDiff.setVisibility(View.VISIBLE);
        }

    }

    //this function is for updating the display of the chosen alarm time
    private  void updateTimeDisplay(int hour, int mins){

        //this calendar stores the time that the alarm show go off at... only with a 24hr time range
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, mins);

        //if the calendar is set to a time that has already passed today change it to be for tomorrow
        if(System.currentTimeMillis() > calendar.getTimeInMillis()){
            calendar.roll(Calendar.DAY_OF_YEAR, true);
        }

        if(switchOn.isChecked()){
            textAlarmIndex.setVisibility(View.VISIBLE);
            textAlrmDiff.setVisibility(View.VISIBLE);
        }
        else
        {
            textAlarmIndex.setVisibility(View.INVISIBLE);
            textAlrmDiff.setVisibility(View.INVISIBLE);
        }

        //the rest of this function determines the number of minutes until the alarm goes off
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

        if(mins < 10) {
            textAlarmIndex.setText("Alarm set at - " + hour + ":0" + mins);
        } else{
            textAlarmIndex.setText("Alarm set at - " + hour + ":" + mins);
        }
        textAlrmDiff.setText("Hours: " + lDiffHours + " Mins: " + lDiffMins);
    }

    //this function is for creating the calendar/intent/alarm manager
    private void updateCalendar(int hour, int mins){

        //this intent is used to pass the switch state, minutes between alarms, and number of alarms to the pending intent
        Intent mIntent = new Intent(/*this.context*/ getApplicationContext(), AlarmReceiver.class);
        mIntent.putExtra(getResources().getString(R.string.Ex_Alarm_State), switchOn.isChecked());
        mIntent.putExtra(getResources().getString(R.string.Ex_Snooze_Min), seekBarMins.getProgress()+1);
        mIntent.putExtra(getResources().getString(R.string.Ex_Alarm_Num), seekBarAlrms.getProgress());

        //this pending intent is used with the alarm manager to set up the alarm at the correct time
        PendingIntent pIntent = PendingIntent.getBroadcast(MainActivity.this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //this is the calendar for getting the time
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour );
        calendar.set(Calendar.MINUTE, mins);

        //if the calendar is set to a time that has already passed today change it to be for tomorrow
        if(System.currentTimeMillis() > calendar.getTimeInMillis()){
            calendar.roll(Calendar.DAY_OF_YEAR, true);
        }

        //if the switch is on
        if(switchOn.isChecked()){
            alarm_manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        }
        else{
            //Immediantly sends signal to the alarm receiver to ask to stop the belltone
            sendBroadcast(mIntent);

            alarm_manager.cancel(pIntent);
        }

        //go to the function to update the display
        updateTimeDisplay(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }

    @Override
    public void onStop(){
        //store the shared Preferences stuff ie alarm hour, min, numbAlarms, numbMins
        editor.putInt( getResources().getString(R.string.Alarm_Hour) , timePicker.getHour());
        editor.putInt( getResources().getString(R.string.Alarm_Min) , timePicker.getMinute());
        editor.putInt( getResources().getString(R.string.num_Alarm) , seekBarAlrms.getProgress());
        editor.putInt( getResources().getString(R.string.num_Mins) , seekBarMins.getProgress());
        editor.putBoolean( getResources().getString(R.string.alarm_On) , switchOn.isChecked());
        editor.apply();

        super.onStop();

    }


}
