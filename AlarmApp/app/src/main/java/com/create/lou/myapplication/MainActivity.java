package com.create.lou.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.AlarmManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;


import org.w3c.dom.Text;

import java.sql.Time;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Switch switchOn;
    private SeekBar seekBarAlrms, seekBarMins;
    private TimePicker timePicker;
    private TextView textValAlarm, textValMin, textAlarmIndex, textAlrmDiff;

    boolean pastAlarm;

    //making a calendar time so that the alarm can be set to a specific time
    final Calendar calendar = Calendar.getInstance();

    //don't know why this was added
    Context context;

    PendingIntent pIntent;
    Intent mIntent;

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

                updateTimeDisplay( hour, min);
            }
        });


        //This is a listener for the main switch that turns on and off the alarms
        switchOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){


                //These two enabled methods modify the seekBars and only make them work if the main switch is on
                seekBarAlrms.setEnabled(isChecked);
                seekBarMins.setEnabled(isChecked);


                //if the switch is on
                if(isChecked){

                    updateTimeDisplay( timePicker.getHour(), timePicker.getMinute() );

                    //Adding an extra boolean to give the current state of the switch so that you can immediantly turn off the alarm
                    mIntent.putExtra("Alarm State", true);

                    pIntent = PendingIntent.getBroadcast(MainActivity.this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarm_manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);

                    //textAlarmIndex.setText("Alarm set at - " + timePicker.getHour() + ":" + timePicker.getMinute());
                    textAlarmIndex.setVisibility(View.VISIBLE);

                    //textAlrmDiff.setText("Hours: " + lDiffHours + " Mins: " + lDiffMins);
                    textAlrmDiff.setVisibility(View.VISIBLE);



                }
                else{

                    updateTimeDisplay( timePicker.getHour(), timePicker.getMinute());

                    textAlarmIndex.setVisibility(View.INVISIBLE);
                    textAlrmDiff.setVisibility(View.INVISIBLE);

                    mIntent.putExtra("Alarm State", false);

                    //Immediantly sends signal to the alarm receiver to ask to stop the belltone
                    sendBroadcast(mIntent);

                    alarm_manager.cancel(pIntent);

                }
            }
        });

        //This is used to initialize the String value (if I don't have it it says 0 despite starting at 3)
        textValAlarm.setText("Alarms: " + seekBarAlrms.getProgress());
        textValMin.setText("Mins: " + seekBarMins.getProgress());

        //setting up the seekBars so that they can't be equal to 0
        seekBarAlrms.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int alrms = 0;                                                                  //value needs to be initialized in the listener or it crashes the program. this value can be
            @Override                                                                       //access outside the listener through seekbar.getProgress()
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {              //I don't want there to be 0 alarms because then it would not wake me up
                alrms = i + 1;
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

    private void initializeVariables(){                                 //This functions initializes all the variables
        switchOn = (Switch) findViewById(R.id.switchOn);
        seekBarAlrms = (SeekBar) findViewById(R.id.seekBarAlrms);
        seekBarMins = (SeekBar) findViewById(R.id.seekBarMins);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        textValAlarm = (TextView) findViewById(R.id.textValNumAlrms);
        textValMin = (TextView) findViewById(R.id.textValSnoozeMins);
        textAlarmIndex = (TextView) findViewById(R.id.textAlarmInd);
        textAlrmDiff = (TextView) findViewById(R.id.textAlarmDiff);

        seekBarAlrms.setEnabled(false);
        seekBarMins.setEnabled(false);

        textAlarmIndex.setVisibility(View.INVISIBLE);
        textAlrmDiff.setVisibility(View.INVISIBLE);


        timePicker.setIs24HourView(true);
        //timePicker.setHour(7);                                             //These should be changed to the values that were
        //timePicker.setMinute(15);                                         //set the day before so that it doesn't need to be set everyday

        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //Setting up the intent to the AlarmReceiver Class
        mIntent = new Intent(this.context, AlarmReceiver.class);

    }

    //this function is for updating the display of the chosen alarm time
    private  void updateTimeDisplay( int hour, int min){

        long diffMillis, lDiffMins, lDiffHours;

        calendar.set(Calendar.HOUR_OF_DAY, hour );
        calendar.set(Calendar.MINUTE, min );

        if(pastAlarm){
            calendar.roll(Calendar.DAY_OF_YEAR, false);
            pastAlarm = false;
        }

        //if the calendar is set to a time that has already passed today change it to be for tomorrow
        if(System.currentTimeMillis() > calendar.getTimeInMillis()){
            calendar.roll(Calendar.DAY_OF_YEAR, true);
            pastAlarm = true;
        }

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

        textAlarmIndex.setText("Alarm set at - " + hour + ":" + min);
        textAlrmDiff.setText("Hours: " + lDiffHours + " Mins: " + lDiffMins);
    }


}
