package com.create.lou.myapplication;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;

import static android.content.Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP;

/**
 * Created by lou on 9/14/17.
 */

public class BellTonePlayService extends Service {


    boolean isRunning;
    Uri notification;
    Ringtone r;

    Intent snzIntent;// = new Intent(getApplicationContext(),AlarmReceiver.class);

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

        if(!this.isRunning && alarmState){
            Log.i("Local Service", "Starting Alarm");
            notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
            notifyMessage();

            snzIntent = new Intent(getApplicationContext(), SnoozeActivity.class);
            startActivity(snzIntent);

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

    public void notifyMessage(){

        Intent mainIntent = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pMainIntent = PendingIntent.getActivity(this, 0, mainIntent, 0);

        Notification alrmNotify = new android.support.v7.app.NotificationCompat.Builder(this)
                .setContentTitle("Wake Up!")
                .setContentText("Click Here")
                .setContentIntent(pMainIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVibrate( new long[]{1000, 1000})
                .setAutoCancel(true)
                .build();


        NotificationManagerCompat mNM = NotificationManagerCompat.from(this);
        mNM.notify(1, alrmNotify);
    }

}
