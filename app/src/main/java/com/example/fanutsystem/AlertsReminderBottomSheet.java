package com.example.fanutsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * In-app summary of immunization reminders and general alerts (toolbar bell).
 */
public class AlertsReminderBottomSheet extends BottomSheetDialogFragment {

    private static final String TAG = "alerts_reminders";

    public static void show(@NonNull FragmentActivity activity) {
        AlertsReminderBottomSheet sheet = new AlertsReminderBottomSheet();
        sheet.show(activity.getSupportFragmentManager(), TAG);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_alerts_reminders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout vacContainer = view.findViewById(R.id.containerImmunization);
        LinearLayout genContainer = view.findViewById(R.id.containerGeneral);
        MaterialButton btnImm = view.findViewById(R.id.btnOpenImmunization);

        vacContainer.removeAllViews();
        genContainer.removeAllViews();

        List<InAppReminderAggregator.VaccineReminder> vaccines =
                InAppReminderAggregator.collectVaccinationReminders(requireContext());

        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

        if (vaccines.isEmpty()) {
            TextView empty = new TextView(requireContext());
            empty.setText(R.string.alerts_sheet_empty_immunization);
            empty.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
            empty.setTextSize(14);
            int pad = (int) (8 * getResources().getDisplayMetrics().density);
            empty.setPadding(pad, 0, pad, pad);
            vacContainer.addView(empty);
        } else {
            LayoutInflater inflater = LayoutInflater.from(requireContext());
            for (InAppReminderAggregator.VaccineReminder r : vaccines) {
                View row = inflater.inflate(R.layout.item_alert_reminder_row, vacContainer, false);
                TextView tvTitle = row.findViewById(R.id.tvRowTitle);
                TextView tvDetail = row.findViewById(R.id.tvRowDetail);
                TextView tvBadge = row.findViewById(R.id.tvRowBadge);

                tvTitle.setText(r.doseDisplayName);
                String dueStr = df.format(new Date(r.dueMillis));
                tvDetail.setText(getString(R.string.alerts_vaccine_row_detail_fmt, r.childName, dueStr));

                if (r.urgency == InAppReminderAggregator.VaccineUrgency.OVERDUE) {
                    tvBadge.setVisibility(View.VISIBLE);
                    tvBadge.setText(R.string.vaccine_status_overdue);
                    tvBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_danger));
                } else {
                    tvBadge.setVisibility(View.VISIBLE);
                    tvBadge.setText(R.string.vaccine_status_due_soon);
                    tvBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_warning));
                }
                vacContainer.addView(row);
            }
        }

        List<String> general = generalAlertLines();
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (String line : general) {
            View row = inflater.inflate(R.layout.item_alert_reminder_row, genContainer, false);
            TextView tvTitle = row.findViewById(R.id.tvRowTitle);
            TextView tvDetail = row.findViewById(R.id.tvRowDetail);
            TextView tvBadge = row.findViewById(R.id.tvRowBadge);
            tvBadge.setVisibility(View.GONE);
            tvTitle.setVisibility(View.GONE);
            tvDetail.setText(line);
            genContainer.addView(row);
        }

        btnImm.setOnClickListener(v -> {
            dismiss();
            startActivity(new Intent(requireContext(), VaccinationActivity.class));
        });
    }

    private List<String> generalAlertLines() {
        List<String> lines = new java.util.ArrayList<>();
        lines.add(getString(R.string.alert_general_tip_facility));
        if (ChildStorage.getChildren(requireContext()).isEmpty()) {
            lines.add(getString(R.string.alert_general_register_child));
        }
        lines.add(getString(R.string.alert_general_system_notifications));
        return lines;
    }
}
