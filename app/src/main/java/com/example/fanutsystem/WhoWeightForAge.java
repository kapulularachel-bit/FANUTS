package com.example.fanutsystem;

/**
 * WHO Child Growth Standards — weight-for-age (kg), simplified anchor tables
 * with linear interpolation between months. Values approximate official boys/girls
 * median, −2 SD and −3 SD curves for ages 0–60 months (use clinical charts for decisions).
 */
public final class WhoWeightForAge {

    private static final float[] MONTHS = {0f, 6f, 12f, 18f, 24f, 36f, 48f, 60f};

    private static final double[] BOYS_MEDIAN =
            {3.35, 7.90, 9.65, 10.90, 12.15, 14.35, 16.35, 18.25};
    private static final double[] BOYS_MINUS_2_SD =
            {2.54, 6.45, 7.85, 8.95, 9.95, 11.75, 13.45, 15.05};
    private static final double[] BOYS_MINUS_3_SD =
            {2.27, 5.82, 7.08, 8.05, 9.02, 10.68, 12.22, 13.68};

    private static final double[] GIRLS_MEDIAN =
            {3.23, 7.30, 8.95, 10.15, 11.45, 13.55, 15.35, 17.05};
    private static final double[] GIRLS_MINUS_2_SD =
            {2.45, 5.95, 7.28, 8.28, 9.36, 11.08, 12.52, 13.85};
    private static final double[] GIRLS_MINUS_3_SD =
            {2.19, 5.37, 6.56, 7.46, 8.46, 10.02, 11.35, 12.55};

    private WhoWeightForAge() {}

    /** Age in completed months, clamped to 0–60 for WHO 0–5y charts. */
    public static int clampAgeMonthsForWho(int ageMonths) {
        return Math.max(0, Math.min(60, ageMonths));
    }

    public static double medianKg(boolean girl, double ageMonths) {
        return interpolate(ageMonths, girl ? GIRLS_MEDIAN : BOYS_MEDIAN);
    }

    public static double minus2SdKg(boolean girl, double ageMonths) {
        return interpolate(ageMonths, girl ? GIRLS_MINUS_2_SD : BOYS_MINUS_2_SD);
    }

    public static double minus3SdKg(boolean girl, double ageMonths) {
        return interpolate(ageMonths, girl ? GIRLS_MINUS_3_SD : BOYS_MINUS_3_SD);
    }

    private static double interpolate(double ageMonths, double[] values) {
        if (ageMonths <= MONTHS[0]) {
            return values[0];
        }
        int last = MONTHS.length - 1;
        if (ageMonths >= MONTHS[last]) {
            return values[last];
        }
        for (int i = 0; i < last; i++) {
            if (ageMonths <= MONTHS[i + 1]) {
                float span = MONTHS[i + 1] - MONTHS[i];
                double t = (ageMonths - MONTHS[i]) / span;
                return values[i] + t * (values[i + 1] - values[i]);
            }
        }
        return values[last];
    }
}
