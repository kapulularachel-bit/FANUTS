package com.example.fanutsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class ChildModuleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_module);

        // Link card layouts as buttons from the layout XML
        View btnRegister = findViewById(R.id.btnRegisterLayout);
        View btnView = findViewById(R.id.btnViewLayout);
        View btnGrowth = findViewById(R.id.btnGrowthLayout);
        View btnMuac = findViewById(R.id.btnMuacLayout);
        View btnDanger = findViewById(R.id.btnDangerLayout);
        View btnVaccination = findViewById(R.id.btnVaccinationLayout);
        MaterialButton btnEmergency = findViewById(R.id.btnEmergencyShortcut);

        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> {
                Intent intent = new Intent(ChildModuleActivity.this, ChildRegistrationActivity.class);
                startActivity(intent);
            });
        }

        if (btnView != null) {
            btnView.setOnClickListener(v -> {
                Intent intent = new Intent(ChildModuleActivity.this, DashboardActivity.class);
                startActivity(intent);
            });
        }

        if (btnGrowth != null) {
            btnGrowth.setOnClickListener(v -> {
                Intent intent = new Intent(ChildModuleActivity.this, GrowthMonitoringActivity.class);
                startActivity(intent);
            });
        }

        if (btnMuac != null) {
            btnMuac.setOnClickListener(v -> {
                Intent intent = new Intent(ChildModuleActivity.this, MuacActivity.class);
                startActivity(intent);
            });
        }

        if (btnDanger != null) {
            btnDanger.setOnClickListener(v -> {
                Intent intent = new Intent(ChildModuleActivity.this, IMCIActivity.class);
                startActivity(intent);
            });
        }

        if (btnVaccination != null) {
            btnVaccination.setOnClickListener(v -> {
                Intent intent = new Intent(ChildModuleActivity.this, VaccinationActivity.class);
                startActivity(intent);
            });
        }

        if (btnEmergency != null) {
            btnEmergency.setOnClickListener(v -> {
                Intent intent = new Intent(ChildModuleActivity.this, EmergencyActivity.class);
                startActivity(intent);
            });
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
