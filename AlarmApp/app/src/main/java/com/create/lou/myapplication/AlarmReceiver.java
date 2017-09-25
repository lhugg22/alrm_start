package com.create.lou.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.util.Log;

/**
 * Created by lou on 9/14/17.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){


        Log.e("We are in the receiver", "Made it");


        //this is used to get the the extra boolean we passed from the intent in the main class
        Boolean getAlarmState = intent.getExtras().getBoolean("Alarm State");

        //Making an intent to go to the BellTonePlayService
        Intent mIntent = new Intent(context, BellTonePlayService.class);

        //used to pass the current switch state to the BellTonePlayer so that the alarm can be immediantly stopped
        mIntent.putExtra("Alarm State", getAlarmState);

        context.startService(mIntent);



        Log.e("Thru Receiver", "On to Play Service");
    }


}
