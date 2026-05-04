package com.example.fanutsystem;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class VaccinationReminderWorker extends Worker {

    public VaccinationReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context ctx = getApplicationContext();
        NotificationHelper.createNotificationChannel(ctx);
        NotificationHelper.createVaccinationChannel(ctx);
        VaccinationReminderChecker.run(ctx);
        return Result.success();
    }
}
