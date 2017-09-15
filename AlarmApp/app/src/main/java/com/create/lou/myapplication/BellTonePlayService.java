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

        //I might have to change the getAppContext to context passed by the intent?

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        //r.play();


        if(alarmState) {
            //this code snippet is used to play the default alarm tone
            r.play();
        }
        else {
            r.stop();
        }


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "on Destroy called", Toast.LENGTH_SHORT).show();
    }



}
