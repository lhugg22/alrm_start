package com.create.lou.myapplication;


import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by lou on 9/14/17.
 */

public class BellTonePlayService extends Service {


    boolean isRunning;


    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.i("Local Service", "Received Start id " + startId + ":" + intent);

        Boolean alarmState = intent.getExtras().getBoolean("Alarm State");

        Log.i("Local Service", "Alarm State: " +  alarmState);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);


        if(!this.isRunning && alarmState){
            Log.i("Local Service", "Starting Alarm");
            r.play();
            isRunning = true;
        } else if(this.isRunning && !alarmState){
            r.stop();
            Log.i("Local Service", "Stopping Alarm");
            isRunning = false;
        } else if(!this.isRunning && !alarmState){
            Log.i("Local Service", "Not Alarming, why am I in PlayService");
        } else if(this.isRunning && alarmState){
            Log.i("Local Service", "Alarm Running");
        }


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "on Destroy called", Toast.LENGTH_SHORT).show();
    }



}
