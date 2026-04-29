package com.example.fanutsystem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.List;

public class IMCIActivity extends AppCompatActivity {

    private DangerSignAdapter adapter;
    private List<DangerSignAdapter.DangerSign> dangerSigns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imci);

        // Setup Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Initialize Knowledge Base Danger Signs
        dangerSigns = KnowledgeBase.getCriticalDangerSigns();
        adapter = new DangerSignAdapter(dangerSigns);

        ListView listView = findViewById(R.id.lvDangerSigns);
        listView.setAdapter(adapter);

        Button btnEvaluate = findViewById(R.id.btnEvaluate);
        if (btnEvaluate != null) {
            btnEvaluate.setOnClickListener(v -> evaluateConditions());
        }
    }

    private void evaluateConditions() {
        boolean hasDangerSign = false;
        StringBuilder selectedSignsText = new StringBuilder();

        for (DangerSignAdapter.DangerSign sign : dangerSigns) {
            if (sign.isSelected()) {
                hasDangerSign = true;
                selectedSignsText.append("• ").append(sign.getTitle()).append("\n");
            }
        }

        if (hasDangerSign) {
            showReferralAlert(selectedSignsText.toString());
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Assessment Complete")
                    .setMessage(R.string.no_danger_signs)
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    private void showReferralAlert(String signs) {
        new AlertDialog.Builder(this)
                .setTitle("⚠️ URGENT REFERRAL REQUIRED")
                .setMessage("The child has one or more danger signs. Please take the child to the nearest health facility IMMEDIATELY.\n\nSigns observed:\n" + signs)
                .setPositiveButton("Find Facility", (dialog, which) -> showFacilitiesList())
                .setNegativeButton("Dismiss", (dialog, which) -> {
                    // Send follow-up reminder if they ignore the alert
                    NotificationHelper.sendFollowUpReminder(this);
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show();
    }

    private void showFacilitiesList() {
        List<KnowledgeBase.HealthFacility> facilities = KnowledgeBase.getNearbyHealthFacilities();
        String[] facilityNames = new String[facilities.size()];
        for (int i = 0; i < facilities.size(); i++) {
            facilityNames[i] = facilities.get(i).toString();
        }

        new AlertDialog.Builder(this)
                .setTitle("Nearby Health Facilities")
                .setItems(facilityNames, (dialog, which) -> {
                    String contact = facilities.get(which).getContact();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + contact));
                    startActivity(intent);
                })
                .setNegativeButton("Back", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
