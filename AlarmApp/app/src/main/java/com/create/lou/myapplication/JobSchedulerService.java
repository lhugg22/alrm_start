package com.create.lou.myapplication;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by lou on 9/13/17.
 *
 * Adding comments to the code to see if there is a difference
 */

public class JobSchedulerService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params){
        mJobHandler.sendMessage(Message.obtain(mJobHandler, 1, params) );
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters pararms){
        mJobHandler.removeMessages( 1 );
        return false;
    }

    private Handler mJobHandler = new Handler(new Handler.Callback(){

        @Override
        public boolean handleMessage(Message msg){
            Toast.makeText(getApplicationContext(), "Job Service Task Running", Toast.LENGTH_SHORT).show();
            jobFinished( (JobParameters) msg.obj, false);
            return true;
        }
    });
}
