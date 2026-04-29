package com.example.fanutsystem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class EmergencyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // 1. National Ambulance Shortcut
        findViewById(R.id.btnCallAmbulance).setOnClickListener(v -> makeCall("998"));

        // 2. Health Worker Shortcut
        findViewById(R.id.btnCallWorker).setOnClickListener(v -> makeCall("+265888123456"));

        // 3. Populate Nearby Facilities
        LinearLayout container = findViewById(R.id.facilityContainer);
        List<KnowledgeBase.HealthFacility> facilities = KnowledgeBase.getNearbyHealthFacilities();

        for (KnowledgeBase.HealthFacility facility : facilities) {
            MaterialButton btn = new MaterialButton(this, null, com.google.android.material.R.attr.materialButtonStyle);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 16);
            btn.setLayoutParams(params);
            btn.setText("Call " + facility.getName());
            btn.setAllCaps(false);
            btn.setIcon(getDrawable(android.R.drawable.ic_menu_call));
            btn.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
            btn.setBackgroundTintList(getColorStateList(R.color.white));
            btn.setTextColor(getColor(R.color.text_main));
            btn.setStrokeWidth(2);
            btn.setStrokeColor(getColorStateList(R.color.gray_button));
            btn.setCornerRadius(16);
            
            btn.setOnClickListener(v -> makeCall(facility.getContact()));
            container.addView(btn);
        }
    }

    private void makeCall(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }
}
