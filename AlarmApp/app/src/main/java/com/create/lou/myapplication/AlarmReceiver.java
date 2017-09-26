package com.create.lou.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by lou on 9/14/17.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){


        Log.e("We are in the receiver", "Made it");

        //Making an intent to go to the BellTonePlayService
        Intent mIntent = new Intent(context, BellTonePlayService.class);

        //used to pass the current switch state to the BellTonePlayer so that the alarm can be immediantly stopped
        mIntent.putExtra("Alarm State", intent.getExtras().getBoolean("Alarm State"));
        mIntent.putExtra("Snooze Minutes", intent.getExtras().getInt("Snooze Minutes"));
        mIntent.putExtra("Number of Alarms", intent.getExtras().getInt("Number of Alarms"));

        context.startService(mIntent);



        Log.e("Thru Receiver", "On to Play Service");
    }


}
