package com.example.fanutsystem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class EmergencyActivity extends AppCompatActivity {

    private static final int FACILITY_META_MAX_LEN = 140;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        findViewById(R.id.btnCallAmbulance).setOnClickListener(v -> makeCall("998"));
        findViewById(R.id.btnCallWorker).setOnClickListener(v -> makeCall("+265888123456"));

        LinearLayout container = findViewById(R.id.facilityContainer);
        LayoutInflater inflater = LayoutInflater.from(this);
        List<KnowledgeBase.HealthFacility> facilities = KnowledgeBase.getNearbyHealthFacilities();

        for (KnowledgeBase.HealthFacility facility : facilities) {
            MaterialCardView card =
                    (MaterialCardView) inflater.inflate(R.layout.item_emergency_facility_card, container, false);

            TextView tvName = card.findViewById(R.id.tvFacilityName);
            TextView tvMeta = card.findViewById(R.id.tvFacilityMeta);
            TextView tvPhone = card.findViewById(R.id.tvFacilityPhone);

            tvName.setText(facility.getName());
            tvPhone.setText(facility.getContact());

            String services = facility.getServices() != null ? facility.getServices() : "";
            String meta = getString(R.string.emergency_facility_services_fmt,
                    facility.getDistrict(), services);
            if (meta.length() > FACILITY_META_MAX_LEN) {
                meta = meta.substring(0, FACILITY_META_MAX_LEN - 1).trim() + "…";
            }
            tvMeta.setText(meta);

            card.setOnClickListener(v -> makeCall(facility.getContact()));
            card.setContentDescription(getString(R.string.emergency_dial_facility_cd, facility.getName()));

            container.addView(card);
        }

        NavigationUtils.setupBottomNavigation(this, R.id.nav_emergency);
    }

    private void makeCall(String rawNumber) {
        if (rawNumber == null || rawNumber.isEmpty()) {
            return;
        }
        String sanitized = sanitizedPhone(rawNumber);
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.fromParts("tel", sanitized, null));
        startActivity(intent);
    }

    private static String sanitizedPhone(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.replaceAll("\\s+", "");
    }
}
