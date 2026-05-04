package com.example.fanutsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class ChildModuleActivity extends AppCompatActivity {

    private TextView tvStatChildrenCount;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_module);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
            NotificationMenuHelper.attach(this, toolbar);
        }
        tvStatChildrenCount = findViewById(R.id.tvStatChildrenCount);
        GridLayout gridTools = findViewById(R.id.gridTools);
        configureToolGrid(gridTools);

        // Link card layouts as buttons from the layout XML
        View btnRegister = findViewById(R.id.btnRegisterLayout);
        View btnView = findViewById(R.id.btnViewLayout);
        View btnGrowth = findViewById(R.id.btnGrowthLayout);
        View btnMuac = findViewById(R.id.btnMuacLayout);
        View btnDanger = findViewById(R.id.btnDangerLayout);
        View btnVaccination = findViewById(R.id.btnVaccinationLayout);

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

        // Setup Bottom Navigation
        NavigationUtils.setupBottomNavigation(this, R.id.nav_child);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshOverviewStats();
        if (toolbar != null) {
            NotificationMenuHelper.refreshBadge(this, toolbar);
        }
    }

    private void refreshOverviewStats() {
        if (tvStatChildrenCount == null) {
            return;
        }
        List<Child> children = ChildStorage.getChildren(this);
        tvStatChildrenCount.setText(String.valueOf(children.size()));
    }

    private void configureToolGrid(GridLayout grid) {
        if (grid == null) {
            return;
        }
        int sw = getResources().getConfiguration().smallestScreenWidthDp;
        grid.setColumnCount(sw >= 600 ? 3 : 2);
    }
}
