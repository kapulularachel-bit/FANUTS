package com.example.fanutsystem;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * At most one summary notification per calendar day if there is overdue or imminent work.
 */
public final class VaccinationReminderChecker {

    private static final String PREF = "vaccination_notifier";
    private static final String KEY_LAST_DAY = "last_notify_yyyy_mm_dd";

    private VaccinationReminderChecker() {}

    public static void run(Context context) {
        Context app = context.getApplicationContext();
        SharedPreferences prefs = app.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        if (today.equals(prefs.getString(KEY_LAST_DAY, ""))) {
            return;
        }

        long startToday = startOfTodayMillis();
        long weekAhead = startToday + 7L * 24 * 60 * 60 * 1000;

        int overdue = 0;
        int upcoming = 0;
        String sampleChild = null;

        List<Child> children = ChildStorage.getChildren(app);
        for (Child child : children) {
            List<VaccinationRepository.VaccineUiModel> doses = VaccinationRepository.loadSchedule(app, child);
            for (VaccinationRepository.VaccineUiModel d : doses) {
                if (d.isCompleted()) {
                    continue;
                }
                if (d.getDueDateMillis() < startToday) {
                    overdue++;
                    if (sampleChild == null) {
                        sampleChild = child.getName();
                    }
                } else if (d.getDueDateMillis() <= weekAhead) {
                    upcoming++;
                    if (sampleChild == null) {
                        sampleChild = child.getName();
                    }
                }
            }
        }

        if (overdue == 0 && upcoming == 0) {
            return;
        }

        prefs.edit().putString(KEY_LAST_DAY, today).apply();

        String title = app.getString(R.string.notify_vaccination_title);
        String body;
        String who = (sampleChild != null && !sampleChild.isEmpty())
                ? sampleChild
                : app.getString(R.string.notify_vaccination_generic_child);
        if (overdue > 0) {
            body = app.getString(R.string.notify_vaccination_overdue_body, overdue, who);
        } else {
            body = app.getString(R.string.notify_vaccination_upcoming_body, upcoming, who);
        }
        NotificationHelper.notifyVaccinationReminder(app, title, body);
    }

    private static long startOfTodayMillis() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }
}
