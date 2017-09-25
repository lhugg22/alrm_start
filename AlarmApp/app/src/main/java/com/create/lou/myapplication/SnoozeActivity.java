package com.create.lou.myapplication;

import android.app.WallpaperManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;

public class SnoozeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snooze);

        final WallpaperManager wpManager = WallpaperManager.getInstance(this);
        final Drawable wpDrawable = wpManager.getDrawable();

        //LinearLayoutCompat ll = (LinearLayoutCompat) findViewById(R.layout.activity_snooze);
        //ll.setBackground(wpDrawable);

    }
}
