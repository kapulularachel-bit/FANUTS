package com.example.fanutsystem;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public final class VaccinationWorkScheduler {

    private static final String UNIQUE_NAME = "immunization_daily_reminder";

    private VaccinationWorkScheduler() {}

    /** Daily background check (best-effort; exact timing may batch with Doze). */
    public static void schedule(Context context) {
        Constraints constraints = new Constraints.Builder().build();
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                VaccinationReminderWorker.class,
                1,
                TimeUnit.DAYS)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context.getApplicationContext())
                .enqueueUniquePeriodicWork(
                        UNIQUE_NAME,
                        ExistingPeriodicWorkPolicy.KEEP,
                        request);
    }
}
