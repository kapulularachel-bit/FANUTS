package com.example.fanutsystem;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.util.ArrayList;
import java.util.List;

public class VaccinationActivity extends AppCompatActivity {

    private VaccinationAdapter adapter;
    private List<VaccinationAdapter.Vaccine> vaccinations;
    private TextView tvCompletedCount, tvProgressPercent;
    private LinearProgressIndicator progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccination);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        tvCompletedCount = findViewById(R.id.tvCompletedCount);
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        progressBar = findViewById(R.id.vaccineProgressBar);

        ListView listView = findViewById(R.id.vaccinationListView);
        
        vaccinations = new ArrayList<>();
        vaccinations.add(new VaccinationAdapter.Vaccine("BCG", "At Birth"));
        vaccinations.add(new VaccinationAdapter.Vaccine("OPV 0", "At Birth"));
        vaccinations.add(new VaccinationAdapter.Vaccine("OPV 1", "6 Weeks"));
        vaccinations.add(new VaccinationAdapter.Vaccine("DPT-HepB-Hib 1", "6 Weeks"));
        vaccinations.add(new VaccinationAdapter.Vaccine("PCV 1", "6 Weeks"));
        vaccinations.add(new VaccinationAdapter.Vaccine("Rotavirus 1", "6 Weeks"));
        vaccinations.add(new VaccinationAdapter.Vaccine("OPV 2", "10 Weeks"));
        vaccinations.add(new VaccinationAdapter.Vaccine("DPT-HepB-Hib 2", "10 Weeks"));
        vaccinations.add(new VaccinationAdapter.Vaccine("PCV 2", "10 Weeks"));
        vaccinations.add(new VaccinationAdapter.Vaccine("Rotavirus 2", "10 Weeks"));
        vaccinations.add(new VaccinationAdapter.Vaccine("OPV 3", "14 Weeks"));
        vaccinations.add(new VaccinationAdapter.Vaccine("DPT-HepB-Hib 3", "14 Weeks"));
        vaccinations.add(new VaccinationAdapter.Vaccine("PCV 3", "14 Weeks"));
        vaccinations.add(new VaccinationAdapter.Vaccine("IPV", "14 Weeks"));
        vaccinations.add(new VaccinationAdapter.Vaccine("Measles-Rubella 1", "9 Months"));
        vaccinations.add(new VaccinationAdapter.Vaccine("Vitamin A 1", "6 Months"));
        vaccinations.add(new VaccinationAdapter.Vaccine("Measles-Rubella 2", "18 Months"));

        adapter = new VaccinationAdapter(vaccinations, this::updateProgress);
        listView.setAdapter(adapter);

        updateProgress();
    }

    private void updateProgress() {
        int completed = 0;
        for (VaccinationAdapter.Vaccine v : vaccinations) {
            if (v.isCompleted()) completed++;
        }

        int total = vaccinations.size();
        int percent = (int) (((float) completed / total) * 100);

        tvCompletedCount.setText(getString(R.string.vaccine_count_format, completed, total));
        tvProgressPercent.setText(getString(R.string.percent_format, percent));
        progressBar.setProgress(percent, true);
    }
}
