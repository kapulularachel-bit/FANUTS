package com.example.fanutsystem;

import android.content.Context;

import androidx.core.content.ContextCompat;

/**
 * MUAC interpretation for children 6–59 months (standard colour bands).
 * Cut-offs: &lt;11.5 cm SAM, 11.5–&lt;12.5 cm MAM, ≥12.5 cm normal on typical tapes;
 * some programmes use 13.5 cm — see tape notes returned with classification.
 */
public final class MuacClassification {

    public static final double SAM_UPPER_CM = 11.5;
    public static final double MAM_UPPER_CM = 12.5;

    public static class MuacResult {
        public final String statusLabel;
        public final int color;
        /** Clinical / programme notes (ranges, age scope, tape). */
        public final String detailText;

        public MuacResult(String statusLabel, int color, String detailText) {
            this.statusLabel = statusLabel;
            this.color = color;
            this.detailText = detailText;
        }
    }

    private MuacClassification() {}

    /** Backward-compatible: no age context. */
    public static MuacResult classify(Context context, double muacCm) {
        return classify(context, muacCm, null);
    }

    /**
     * @param ageMonths optional; if outside 6–59, an age-scope note is prepended.
     */
    public static MuacResult classify(Context context, double muacCm, Integer ageMonths) {
        String status;
        int color;
        if (muacCm < SAM_UPPER_CM) {
            status = context.getString(R.string.muac_severe);
            color = ContextCompat.getColor(context, R.color.status_danger);
        } else if (muacCm < MAM_UPPER_CM) {
            status = context.getString(R.string.muac_moderate);
            color = ContextCompat.getColor(context, R.color.status_warning);
        } else {
            status = context.getString(R.string.muac_normal);
            color = ContextCompat.getColor(context, R.color.status_success);
        }

        StringBuilder detail = new StringBuilder();
        if (ageMonths != null && (ageMonths < 6 || ageMonths > 59)) {
            detail.append(context.getString(R.string.muac_age_scope_warning)).append("\n\n");
        }
        detail.append(context.getString(R.string.muac_reference_ranges_body));
        detail.append("\n\n").append(getTapeNote(context, muacCm));

        return new MuacResult(status, color, detail.toString());
    }

    static String getTapeNote(Context context, double muacCm) {
        String general = context.getString(R.string.muac_tape_general_note);
        if (muacCm >= MAM_UPPER_CM && muacCm < 13.5) {
            return context.getString(R.string.muac_tape_13_5_note) + " " + general;
        }
        return general;
    }
}
