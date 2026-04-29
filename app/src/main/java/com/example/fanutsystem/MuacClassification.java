package com.example.fanutsystem;

import android.content.Context;
import androidx.core.content.ContextCompat;

public class MuacClassification {

    public static class MuacResult {
        public String status;
        public int color;

        public MuacResult(String status, int color) {
            this.status = status;
            this.color = color;
        }
    }

    public static MuacResult classify(Context context, double muac) {
        if (muac < 11.5) {
            return new MuacResult(
                    context.getString(R.string.muac_severe),
                    ContextCompat.getColor(context, android.R.color.holo_red_dark)
            );
        } else if (muac < 12.5) {
            return new MuacResult(
                    context.getString(R.string.muac_moderate),
                    ContextCompat.getColor(context, android.R.color.holo_orange_dark)
            );
        } else {
            return new MuacResult(
                    context.getString(R.string.muac_normal),
                    ContextCompat.getColor(context, android.R.color.holo_green_dark)
            );
        }
    }
}
