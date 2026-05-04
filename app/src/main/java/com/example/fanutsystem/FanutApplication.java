package com.example.fanutsystem;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class FanutApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        NotificationHelper.createNotificationChannel(this);
        NotificationHelper.createVaccinationChannel(this);
        VaccinationWorkScheduler.schedule(this);
    }
}
