package com.example.fanutsystem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Malawi-style infant EPI timing from date of birth (approximation for reminders).
 * Week-based doses use completed days from birth; month-based use calendar months.
 */
public final class EpiSchedule {

    public static final class ScheduledDose {
        public final String code;
        public final String displayName;
        public final String intervalLabel;
        public final long dueDateMillis;
        /** Groups doses into facility “contacts” (birth, 6-week, etc.). */
        public final int encounterOrder;

        ScheduledDose(String code, String displayName, String intervalLabel, long dueDateMillis,
                      int encounterOrder) {
            this.code = code;
            this.displayName = displayName;
            this.intervalLabel = intervalLabel;
            this.dueDateMillis = dueDateMillis;
            this.encounterOrder = encounterOrder;
        }
    }

    private EpiSchedule() {}

    public static Calendar parseBirthCalendar(String dobIso) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdf.setLenient(false);
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(dobIso));
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static List<ScheduledDose> buildSchedule(String dobIso) throws ParseException {
        Calendar b = parseBirthCalendar(dobIso);
        List<ScheduledDose> list = new ArrayList<>();

        int eBirth = 0;
        int e6w = 1;
        int e10w = 2;
        int e14w = 3;
        int e6m = 4;
        int e9m = 5;
        int e18m = 6;

        long birth = addDays(b, 0);
        list.add(new ScheduledDose("BCG", "BCG", "At birth", birth, eBirth));
        list.add(new ScheduledDose("OPV0", "OPV 0", "At birth", birth, eBirth));

        long w6 = addDays(b, 42);
        list.add(new ScheduledDose("OPV1", "OPV 1", "6 weeks", w6, e6w));
        list.add(new ScheduledDose("DPT1", "Penta 1 (DPT-HepB-Hib)", "6 weeks", w6, e6w));
        list.add(new ScheduledDose("PCV1", "PCV 1", "6 weeks", w6, e6w));
        list.add(new ScheduledDose("ROTA1", "Rotavirus 1", "6 weeks", w6, e6w));

        long w10 = addDays(b, 70);
        list.add(new ScheduledDose("OPV2", "OPV 2", "10 weeks", w10, e10w));
        list.add(new ScheduledDose("DPT2", "Penta 2 (DPT-HepB-Hib)", "10 weeks", w10, e10w));
        list.add(new ScheduledDose("PCV2", "PCV 2", "10 weeks", w10, e10w));
        list.add(new ScheduledDose("ROTA2", "Rotavirus 2", "10 weeks", w10, e10w));

        long w14 = addDays(b, 98);
        list.add(new ScheduledDose("OPV3", "OPV 3", "14 weeks", w14, e14w));
        list.add(new ScheduledDose("DPT3", "Penta 3 (DPT-HepB-Hib)", "14 weeks", w14, e14w));
        list.add(new ScheduledDose("PCV3", "PCV 3", "14 weeks", w14, e14w));
        list.add(new ScheduledDose("IPV", "IPV", "14 weeks", w14, e14w));

        list.add(new ScheduledDose("VITA1", "Vitamin A (1st dose)", "6 months", addMonths(b, 6), e6m));
        list.add(new ScheduledDose("MR1", "Measles–Rubella 1", "9 months", addMonths(b, 9), e9m));
        list.add(new ScheduledDose("MR2", "Measles–Rubella 2", "18 months", addMonths(b, 18), e18m));

        return list;
    }

    private static long addDays(Calendar birth, int days) {
        Calendar c = (Calendar) birth.clone();
        c.add(Calendar.DATE, days);
        return c.getTimeInMillis();
    }

    private static long addMonths(Calendar birth, int months) {
        Calendar c = (Calendar) birth.clone();
        c.add(Calendar.MONTH, months);
        return c.getTimeInMillis();
    }
}
