package com.example.fanutsystem;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Collects in-app reminders shown in the alerts sheet and toolbar badge (immunization-focused).
 */
public final class InAppReminderAggregator {

    private InAppReminderAggregator() {}

    public enum VaccineUrgency {
        OVERDUE,
        DUE_SOON
    }

    public static final class VaccineReminder {
        public final String childName;
        public final String doseDisplayName;
        public final long dueMillis;
        public final VaccineUrgency urgency;

        VaccineReminder(String childName, String doseDisplayName, long dueMillis, VaccineUrgency urgency) {
            this.childName = childName;
            this.doseDisplayName = doseDisplayName;
            this.dueMillis = dueMillis;
            this.urgency = urgency;
        }
    }

    public static List<VaccineReminder> collectVaccinationReminders(Context context) {
        Context app = context.getApplicationContext();
        long startToday = startOfTodayMillis();
        long weekAhead = startToday + 7L * 24 * 60 * 60 * 1000L;

        List<VaccineReminder> out = new ArrayList<>();
        for (Child child : ChildStorage.getChildren(app)) {
            List<VaccinationRepository.VaccineUiModel> doses = VaccinationRepository.loadSchedule(app, child);
            for (VaccinationRepository.VaccineUiModel d : doses) {
                if (d.isCompleted()) {
                    continue;
                }
                long due = d.getDueDateMillis();
                if (due < startToday) {
                    out.add(new VaccineReminder(child.getName(), d.getDisplayName(), due, VaccineUrgency.OVERDUE));
                } else if (due <= weekAhead) {
                    out.add(new VaccineReminder(child.getName(), d.getDisplayName(), due, VaccineUrgency.DUE_SOON));
                }
            }
        }

        Comparator<VaccineReminder> cmp = (a, b) -> {
            int byUrg = Integer.compare(urgencyRank(a.urgency), urgencyRank(b.urgency));
            if (byUrg != 0) {
                return byUrg;
            }
            return Long.compare(a.dueMillis, b.dueMillis);
        };
        Collections.sort(out, cmp);
        return out;
    }

    private static int urgencyRank(VaccineUrgency u) {
        return u == VaccineUrgency.OVERDUE ? 0 : 1;
    }

    /** Badge count = overdue + due within 7 days (matches reminder logic). */
    public static int reminderBadgeCount(Context context) {
        return collectVaccinationReminders(context).size();
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
