package com.create.lou.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.AlarmManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private TextView textValAlarm, textValMin;

    Context context;                                            //don't know why this was added

    PendingIntent pIntent;
    Intent mIntent;

    AlarmManager alarm_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);                 //set the file activity_main.xml to be the user interface

        //don't know what this is here but
        this.context = this;


        initializeVariables();                                  //used to initialize all the variable in used in the interface

        final Calendar calendar = Calendar.getInstance();

        switchOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                seekBarAlrms.setEnabled(isChecked);
                seekBarMins.setEnabled(isChecked);

                if(isChecked){
                    //setting calendar to the time picker in the picker
                    calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour() );
                    calendar.set(Calendar.MINUTE, timePicker.getMinute() );


                    pIntent = PendingIntent.getBroadcast(MainActivity.this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarm_manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
                }
                else{
                    alarm_manager.cancel(pIntent);
                }
            }
        });


        textValAlarm.setText("Alarms: " + seekBarAlrms.getProgress());                      //This is used to initialize the String value (if I don't have it it says 0 despite starting at 3)
        textValMin.setText("Mins: " + seekBarMins.getProgress());

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

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            int hour, min = 0;
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                hour = i;
                min = i1;
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

        seekBarAlrms.setEnabled(false);
        seekBarMins.setEnabled(false);

        timePicker.setIs24HourView(true);
        timePicker.setHour(7);                                             //These should be changed to the values that were
        timePicker.setMinute(15);                                         //set the day before so that it doesn't need to be set everyday

        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //Setting up the intent to the AlarmReceiver Class
        mIntent = new Intent(this.context, AlarmReceiver.class);
    }


}
