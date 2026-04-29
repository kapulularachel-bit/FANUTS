package com.example.fanutsystem;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GrowthMonitoringActivity extends AppCompatActivity {

    private EditText etAge, etWeight, etHeight, etMuac;
    private TextView tvResult, tvMuacResult, tvRiskAssessment;
    private LinearLayout resultSection;
    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_growth_monitoring);

        // Setup Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Link UI components
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etMuac = findViewById(R.id.etMuac);
        MaterialButton btnCalculate = findViewById(R.id.btnCalculate);
        
        tvResult = findViewById(R.id.tvResult);
        tvMuacResult = findViewById(R.id.tvMuacResult);
        tvRiskAssessment = findViewById(R.id.tvRiskAssessment);
        resultSection = findViewById(R.id.resultSection);
        lineChart = findViewById(R.id.lineChart);

        btnCalculate.setOnClickListener(v -> calculateMetrics());
    }

    private void calculateMetrics() {
        String ageStr = etAge.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        String muacStr = etMuac.getText().toString().trim();

        if (ageStr.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty() || muacStr.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_all_values), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int ageMonths = Integer.parseInt(ageStr);
            double weight = Double.parseDouble(weightStr);
            double heightCm = Double.parseDouble(heightStr);
            double muacValue = Double.parseDouble(muacStr);

            boolean isCritical = false;
            StringBuilder criticalReasons = new StringBuilder();

            // BMI
            double heightM = heightCm / 100;
            double bmi = weight / (heightM * heightM);
            tvResult.setText(String.format(Locale.getDefault(), "%.1f", bmi));

            // Risk Assessment (Weight-for-Age)
            String riskStatus;
            int riskColor;
            double expectedWeight = 3.2 + (ageMonths * 0.6); 
            if (weight < expectedWeight * 0.75) {
                riskStatus = getString(R.string.risk_high);
                riskColor = ContextCompat.getColor(this, R.color.status_danger);
                isCritical = true;
                criticalReasons.append("• ").append(riskStatus).append("\n");
            } else if (weight < expectedWeight * 0.9) {
                riskStatus = getString(R.string.risk_moderate);
                riskColor = ContextCompat.getColor(this, R.color.status_warning);
            } else {
                riskStatus = getString(R.string.risk_none);
                riskColor = ContextCompat.getColor(this, R.color.status_success);
            }
            tvRiskAssessment.setText(riskStatus);
            tvRiskAssessment.setTextColor(riskColor);

            // MUAC Assessment
            String muacStatusText;
            int muacColor;
            if (muacValue < 11.5) {
                muacStatusText = getString(R.string.muac_severe);
                muacColor = ContextCompat.getColor(this, R.color.status_danger);
                isCritical = true;
                criticalReasons.append("• ").append(muacStatusText).append("\n");
            } else if (muacValue < 12.5) {
                muacStatusText = getString(R.string.muac_moderate);
                muacColor = ContextCompat.getColor(this, R.color.status_warning);
            } else {
                muacStatusText = getString(R.string.muac_normal);
                muacColor = ContextCompat.getColor(this, R.color.status_success);
            }
            tvMuacResult.setText(muacStatusText);
            tvMuacResult.setTextColor(muacColor);

            // Reveal Results and Plot
            showResultCardWithAnimation();
            setupLineChart(ageMonths, (float) weight);

            // Trigger Immediate Alert if Critical (Red Zone)
            if (isCritical) {
                showCriticalAlert(criticalReasons.toString());
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.error_invalid_numbers), Toast.LENGTH_SHORT).show();
        }
    }

    private void showCriticalAlert(String reasons) {
        new AlertDialog.Builder(this)
                .setTitle("⚠️ CRITICAL HEALTH WARNING")
                .setMessage("Your child may be at serious risk. Please visit a health facility immediately.\n\nDetected Risks:\n" + reasons + "\n" + getString(R.string.medical_disclaimer))
                .setPositiveButton("Emergency Help", (dialog, which) -> showEmergencyContacts())
                .setNegativeButton("Dismiss", (dialog, which) -> {
                    NotificationHelper.sendFollowUpReminder(this);
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show();
    }

    private void showEmergencyContacts() {
        List<KnowledgeBase.HealthFacility> contacts = KnowledgeBase.getEmergencyContacts();
        String[] contactNames = new String[contacts.size()];
        for (int i = 0; i < contacts.size(); i++) {
            contactNames[i] = contacts.get(i).toString();
        }

        new AlertDialog.Builder(this)
                .setTitle("Emergency Shortcuts")
                .setItems(contactNames, (dialog, which) -> {
                    String contact = contacts.get(which).getContact();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + contact));
                    startActivity(intent);
                })
                .setNeutralButton("Nearby Facilities", (dialog, which) -> showFacilitiesList())
                .setNegativeButton("Back", null)
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
                .setNegativeButton("Back", (dialog, which) -> showEmergencyContacts())
                .show();
    }

    private void showResultCardWithAnimation() {
        if (resultSection.getVisibility() == View.GONE) {
            resultSection.setVisibility(View.VISIBLE);
            Animation slideUp = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            slideUp.setDuration(600);
            resultSection.startAnimation(slideUp);
        }
    }

    private void setupLineChart(int childAge, float childWeight) {
        lineChart.clear();
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawGridBackground(false);
        lineChart.getLegend().setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(Math.max(24f, childAge + 2));
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(12);
        xAxis.setTextColor(Color.GRAY);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.LTGRAY);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(Math.max(16f, childWeight + 2));
        leftAxis.setTextColor(Color.GRAY);

        lineChart.getAxisRight().setEnabled(false);

        List<Entry> medianPoints = new ArrayList<>();
        List<Entry> sd2Points = new ArrayList<>();
        List<Entry> sd3Points = new ArrayList<>();
        for (int i = 0; i <= (int)xAxis.getAxisMaximum(); i++) {
            float base = 3.3f + (i * 0.45f);
            medianPoints.add(new Entry(i, base));
            sd2Points.add(new Entry(i, base * 0.85f));
            sd3Points.add(new Entry(i, base * 0.75f));
        }

        LineDataSet medianSet = createLineSet(medianPoints, "Median", Color.parseColor("#388E3C"));
        LineDataSet sd2Set = createLineSet(sd2Points, "-2 SD", Color.parseColor("#FBC02D"));
        LineDataSet sd3Set = createLineSet(sd3Points, "-3 SD", Color.parseColor("#D32F2F"));

        List<Entry> childPoints = new ArrayList<>();
        childPoints.add(new Entry(childAge, childWeight));
        LineDataSet childSet = new LineDataSet(childPoints, "Child");
        childSet.setColor(ContextCompat.getColor(this, R.color.pediatric_blue));
        childSet.setCircleColor(ContextCompat.getColor(this, R.color.pediatric_blue));
        childSet.setCircleRadius(8f);
        childSet.setDrawCircleHole(true);
        childSet.setCircleHoleRadius(4f);
        childSet.setDrawValues(true);
        childSet.setValueTextSize(10f);
        childSet.setLineWidth(0f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(medianSet);
        dataSets.add(sd2Set);
        dataSets.add(sd3Set);
        dataSets.add(childSet);

        lineChart.setData(new LineData(dataSets));
        lineChart.animateX(1000);
        lineChart.invalidate();
    }

    private LineDataSet createLineSet(List<Entry> entries, String label, int color) {
        LineDataSet set = new LineDataSet(entries, label);
        set.setColor(color);
        set.setLineWidth(3f);
        set.setDrawCircles(false);
        set.setDrawValues(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        return set;
    }
}
