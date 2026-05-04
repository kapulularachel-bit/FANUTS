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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GrowthMonitoringActivity extends AppCompatActivity {

    private EditText etAge, etWeight, etHeight, etMuac;
    private TextView tvResult, tvMuacResult, tvRiskAssessment, tvMuacClinicalNote, tvWhoAgeNote;
    private LinearLayout resultSection;
    private LineChart lineChart;
    private MaterialButtonToggleGroup toggleGrowthSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_growth_monitoring);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        toggleGrowthSex = findViewById(R.id.toggleGrowthSex);
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etMuac = findViewById(R.id.etMuac);
        MaterialButton btnCalculate = findViewById(R.id.btnCalculate);

        tvResult = findViewById(R.id.tvResult);
        tvMuacResult = findViewById(R.id.tvMuacResult);
        tvRiskAssessment = findViewById(R.id.tvRiskAssessment);
        tvMuacClinicalNote = findViewById(R.id.tvMuacClinicalNote);
        tvWhoAgeNote = findViewById(R.id.tvWhoAgeNote);
        resultSection = findViewById(R.id.resultSection);
        lineChart = findViewById(R.id.lineChart);

        applyIntentPrefill();

        btnCalculate.setOnClickListener(v -> calculateMetrics());
    }

    private void applyIntentPrefill() {
        Intent in = getIntent();
        if (in == null) {
            return;
        }
        String w = in.getStringExtra("child_weight");
        String h = in.getStringExtra("child_height");
        String m = in.getStringExtra("child_muac");
        String dob = in.getStringExtra("child_dob");
        if (w != null && !w.isEmpty()) {
            etWeight.setText(w);
        }
        if (h != null && !h.isEmpty()) {
            etHeight.setText(h);
        }
        if (m != null && !m.isEmpty()) {
            etMuac.setText(m);
        }
        if (dob != null && !dob.isEmpty()) {
            int months = ageMonthsFromIsoDate(dob);
            if (months >= 0) {
                etAge.setText(String.format(Locale.getDefault(), "%d", months));
            }
        }
    }

    /** Completed months from birth date (yyyy-MM-dd); returns -1 if invalid. */
    private int ageMonthsFromIsoDate(String yyyyMmDd) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            sdf.setLenient(false);
            Calendar birth = Calendar.getInstance();
            birth.setTime(sdf.parse(yyyyMmDd));
            Calendar now = Calendar.getInstance();
            int months = (now.get(Calendar.YEAR) - birth.get(Calendar.YEAR)) * 12
                    + (now.get(Calendar.MONTH) - birth.get(Calendar.MONTH));
            if (now.get(Calendar.DAY_OF_MONTH) < birth.get(Calendar.DAY_OF_MONTH)) {
                months--;
            }
            return months;
        } catch (Exception e) {
            return -1;
        }
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

            if (ageMonths < 0 || ageMonths > 240) {
                Toast.makeText(this, getString(R.string.error_invalid_numbers), Toast.LENGTH_SHORT).show();
                return;
            }
            if (weight <= 0 || heightCm <= 0 || muacValue <= 0) {
                Toast.makeText(this, getString(R.string.error_invalid_numbers), Toast.LENGTH_SHORT).show();
                return;
            }

            boolean girl = toggleGrowthSex.getCheckedButtonId() == R.id.btnGrowthSexGirl;
            int ageForWho = WhoWeightForAge.clampAgeMonthsForWho(ageMonths);

            double heightM = heightCm / 100.0;
            double bmi = weight / (heightM * heightM);
            tvResult.setText(String.format(Locale.getDefault(), "%.1f", bmi));

            double wMed3 = WhoWeightForAge.minus3SdKg(girl, ageForWho);
            double wMed2 = WhoWeightForAge.minus2SdKg(girl, ageForWho);

            boolean isCritical = false;
            StringBuilder criticalReasons = new StringBuilder();

            String riskStatus;
            int riskColor;
            if (weight < wMed3) {
                riskStatus = getString(R.string.risk_high);
                riskColor = ContextCompat.getColor(this, R.color.status_danger);
                isCritical = true;
                criticalReasons.append("• ").append(riskStatus).append("\n");
            } else if (weight < wMed2) {
                riskStatus = getString(R.string.risk_moderate);
                riskColor = ContextCompat.getColor(this, R.color.status_warning);
            } else {
                riskStatus = getString(R.string.risk_none);
                riskColor = ContextCompat.getColor(this, R.color.status_success);
            }
            tvRiskAssessment.setText(riskStatus);
            tvRiskAssessment.setTextColor(riskColor);

            if (ageMonths > 60) {
                tvWhoAgeNote.setVisibility(View.VISIBLE);
                tvWhoAgeNote.setText(R.string.growth_age_outside_who_note);
            } else {
                tvWhoAgeNote.setVisibility(View.GONE);
            }

            MuacClassification.MuacResult muacRes =
                    MuacClassification.classify(this, muacValue, ageMonths);
            tvMuacResult.setText(muacRes.statusLabel);
            tvMuacResult.setTextColor(muacRes.color);
            tvMuacClinicalNote.setText(muacRes.detailText);
            tvMuacClinicalNote.setVisibility(View.VISIBLE);

            if (muacValue < MuacClassification.SAM_UPPER_CM) {
                isCritical = true;
                criticalReasons.append("• ").append(muacRes.statusLabel).append("\n");
            }

            showResultCardWithAnimation();
            setupLineChart(girl, Math.min(ageMonths, 60), (float) weight);

            if (isCritical) {
                showCriticalAlert(criticalReasons.toString());
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.error_invalid_numbers), Toast.LENGTH_SHORT).show();
        }
    }

    private void showCriticalAlert(String reasons) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.critical_alert_title)
                .setMessage(getString(R.string.critical_alert_message, reasons)
                        + "\n\n" + getString(R.string.medical_disclaimer))
                .setPositiveButton(R.string.critical_alert_emergency, (dialog, which) -> showEmergencyContacts())
                .setNegativeButton(R.string.critical_alert_dismiss, (dialog, which) ->
                        NotificationHelper.sendFollowUpReminder(this))
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
                .setTitle(R.string.emergency_shortcuts_title)
                .setItems(contactNames, (dialog, which) -> {
                    String contact = contacts.get(which).getContact();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + contact));
                    startActivity(intent);
                })
                .setNeutralButton(R.string.nearby_facilities_btn, (dialog, which) -> showFacilitiesList())
                .setNegativeButton(R.string.back_button_desc, null)
                .show();
    }

    private void showFacilitiesList() {
        List<KnowledgeBase.HealthFacility> facilities = KnowledgeBase.getNearbyHealthFacilities();
        String[] facilityNames = new String[facilities.size()];
        for (int i = 0; i < facilities.size(); i++) {
            facilityNames[i] = facilities.get(i).toString();
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.nearby_facilities_title)
                .setItems(facilityNames, (dialog, which) -> {
                    String contact = facilities.get(which).getContact();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + contact));
                    startActivity(intent);
                })
                .setNegativeButton(R.string.back_button_desc, (dialog, which) -> showEmergencyContacts())
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

    private void setupLineChart(boolean girl, int childAgeMonths, float childWeight) {
        lineChart.clear();
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawGridBackground(false);

        Legend legend = lineChart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(11f);
        legend.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(60f);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7, true);
        xAxis.setTextColor(ContextCompat.getColor(this, R.color.text_tertiary));

        float maxRef = (float) WhoWeightForAge.medianKg(girl, 60);
        float maxY = Math.max(maxRef, childWeight) * 1.15f;
        float minY = 0f;

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(ContextCompat.getColor(this, R.color.stroke_light));
        leftAxis.setAxisMinimum(minY);
        leftAxis.setAxisMaximum(Math.max(8f, maxY));
        leftAxis.setTextColor(ContextCompat.getColor(this, R.color.text_tertiary));

        lineChart.getAxisRight().setEnabled(false);

        List<Entry> medianPoints = new ArrayList<>();
        List<Entry> sd2Points = new ArrayList<>();
        List<Entry> sd3Points = new ArrayList<>();
        for (int m = 0; m <= 60; m++) {
            medianPoints.add(new Entry(m, (float) WhoWeightForAge.medianKg(girl, m)));
            sd2Points.add(new Entry(m, (float) WhoWeightForAge.minus2SdKg(girl, m)));
            sd3Points.add(new Entry(m, (float) WhoWeightForAge.minus3SdKg(girl, m)));
        }

        LineDataSet medianSet = createReferenceLine(medianPoints,
                getString(R.string.legend_median_who), Color.parseColor("#388E3C"));
        LineDataSet sd2Set = createReferenceLine(sd2Points,
                getString(R.string.legend_sd2_who), Color.parseColor("#F9A825"));
        LineDataSet sd3Set = createReferenceLine(sd3Points,
                getString(R.string.legend_sd3_who), Color.parseColor("#D32F2F"));

        List<Entry> childPoints = new ArrayList<>();
        childPoints.add(new Entry(childAgeMonths, childWeight));
        LineDataSet childSet = new LineDataSet(childPoints, getString(R.string.legend_child_point));
        childSet.setColor(ContextCompat.getColor(this, R.color.pediatric_blue));
        childSet.setCircleColor(ContextCompat.getColor(this, R.color.pediatric_blue));
        childSet.setCircleRadius(8f);
        childSet.setDrawCircleHole(true);
        childSet.setCircleHoleRadius(4f);
        childSet.setDrawValues(true);
        childSet.setValueTextSize(10f);
        childSet.setLineWidth(0f);
        childSet.setMode(LineDataSet.Mode.LINEAR);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(medianSet);
        dataSets.add(sd2Set);
        dataSets.add(sd3Set);
        dataSets.add(childSet);

        lineChart.setData(new LineData(dataSets));
        lineChart.animateX(900);
        lineChart.invalidate();
    }

    private LineDataSet createReferenceLine(List<Entry> entries, String label, int color) {
        LineDataSet set = new LineDataSet(entries, label);
        set.setColor(color);
        set.setLineWidth(2.5f);
        set.setDrawCircles(false);
        set.setDrawValues(false);
        set.setMode(LineDataSet.Mode.LINEAR);
        return set;
    }
}
