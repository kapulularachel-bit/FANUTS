package com.example.fanutsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VaccinationActivity extends AppCompatActivity {

    public static final String EXTRA_CHILD_ID = "extra_child_id";

    private TextView tvCompletedCount, tvProgressPercent, tvTotalCount, tvNextDue, tvEmptyState;
    private TextView tvPatientName, tvPatientDobAge, tvPatientSex;
    private LinearProgressIndicator progressBar;
    private View layoutChildPick;
    private Spinner spinnerChildren;

    private RecyclerView recyclerView;
    private VaccinationRecyclerAdapter adapter;
    private List<Child> children = new ArrayList<>();
    private Child selectedChild;

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
        tvTotalCount = findViewById(R.id.tvTotalCount);
        tvNextDue = findViewById(R.id.tvNextDue);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        tvPatientName = findViewById(R.id.tvPatientName);
        tvPatientDobAge = findViewById(R.id.tvPatientDobAge);
        tvPatientSex = findViewById(R.id.tvPatientSex);
        progressBar = findViewById(R.id.vaccineProgressBar);
        layoutChildPick = findViewById(R.id.layoutChildPick);
        spinnerChildren = findViewById(R.id.spinnerChildren);

        recyclerView = findViewById(R.id.recyclerVaccinations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VaccinationRecyclerAdapter(this::onDoseToggled);
        recyclerView.setAdapter(adapter);

        spinnerChildren.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (children.isEmpty()) {
                    return;
                }
                selectedChild = children.get(position);
                bindScheduleForSelected();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedChild = null;
            }
        });

        refreshChildrenUi(getIntent().getStringExtra(EXTRA_CHILD_ID));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        refreshChildrenUi(intent.getStringExtra(EXTRA_CHILD_ID));
    }

    @Override
    protected void onResume() {
        super.onResume();
        String preserveId = selectedChild != null ? selectedChild.getId() : null;
        refreshChildrenUi(preserveId);
    }

    private void refreshChildrenUi(String preferredChildId) {
        children = ChildStorage.getChildren(this);
        if (children.isEmpty()) {
            layoutChildPick.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
            selectedChild = null;
            adapter.submit(new ArrayList<>());
            updateProgressCounts(0, 0);
            return;
        }

        layoutChildPick.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);

        List<String> labels = new ArrayList<>();
        for (Child c : children) {
            labels.add(c.getName());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, labels);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChildren.setAdapter(spinnerAdapter);

        int selectIndex = 0;
        if (preferredChildId != null) {
            for (int i = 0; i < children.size(); i++) {
                if (preferredChildId.equals(children.get(i).getId())) {
                    selectIndex = i;
                    break;
                }
            }
        }
        spinnerChildren.setSelection(selectIndex, false);
        selectedChild = children.get(selectIndex);
        bindScheduleForSelected();
    }

    private void bindPatientStrip() {
        if (selectedChild == null) {
            return;
        }
        tvPatientName.setText(selectedChild.getName());
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        try {
            java.text.SimpleDateFormat iso = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US);
            java.util.Date dob = iso.parse(selectedChild.getDob());
            String dobStr = dob != null ? df.format(dob) : selectedChild.getDob();
            tvPatientDobAge.setText(getString(R.string.immunization_patient_dob_age_fmt,
                    dobStr,
                    formatAgeSummary(selectedChild)));
        } catch (java.text.ParseException e) {
            tvPatientDobAge.setText(selectedChild.getDob());
        }
        String g = selectedChild.getGender();
        if (g != null && !g.trim().isEmpty()) {
            tvPatientSex.setVisibility(View.VISIBLE);
            tvPatientSex.setText(getString(R.string.immunization_sex_fmt, g.trim()));
        } else {
            tvPatientSex.setVisibility(View.GONE);
        }
    }

    private String formatAgeSummary(Child child) {
        int months = child.getAgeInMonths();
        if (months < 0) {
            months = 0;
        }
        if (months >= 24) {
            int y = months / 12;
            int mo = months % 12;
            if (mo > 0) {
                return getString(R.string.immunization_age_years_months_fmt, y, mo);
            }
            return getResources().getQuantityString(R.plurals.immunization_age_years, y, y);
        }
        return getResources().getQuantityString(R.plurals.immunization_age_months, months, months);
    }

    private void bindScheduleForSelected() {
        if (selectedChild == null) {
            adapter.submit(new ArrayList<>());
            updateProgressCounts(0, 0);
            return;
        }

        bindPatientStrip();

        List<VaccinationRepository.VaccineUiModel> flat =
                VaccinationRepository.loadSchedule(this, selectedChild);
        List<VaccinationRepository.RegisterRow> rows =
                VaccinationRepository.loadRegisterRows(this, selectedChild);

        if (flat.isEmpty()) {
            Toast.makeText(this, R.string.vaccination_invalid_dob, Toast.LENGTH_SHORT).show();
            adapter.submit(new ArrayList<>());
            updateProgressCounts(0, 0);
            tvNextDue.setText("");
            return;
        }

        adapter.submit(rows);
        updateProgressFrom(flat);

        VaccinationRepository.VaccineUiModel next =
                VaccinationRepository.findNextIncomplete(flat);
        if (next == null) {
            tvNextDue.setText(R.string.vaccination_all_complete);
        } else {
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
            tvNextDue.setText(getString(R.string.vaccination_next_due_fmt,
                    next.getDisplayName(),
                    df.format(next.getDueDateMillis())));
        }
    }

    private void onDoseToggled(VaccinationRepository.VaccineUiModel dose, boolean completed) {
        if (selectedChild == null) {
            return;
        }
        VaccinationDbHelper.getInstance(this).setCompleted(
                selectedChild.getId(),
                dose.getCode(),
                completed);
        bindScheduleForSelected();
    }

    private void updateProgressFrom(List<VaccinationRepository.VaccineUiModel> list) {
        int completed = 0;
        for (VaccinationRepository.VaccineUiModel v : list) {
            if (v.isCompleted()) {
                completed++;
            }
        }
        updateProgressCounts(completed, list.size());
    }

    private void updateProgressCounts(int completed, int total) {
        tvTotalCount.setText(total > 0 ? "/" + total : "/0");
        int percent = total > 0 ? (int) (((float) completed / total) * 100) : 0;
        tvCompletedCount.setText(String.valueOf(completed));
        tvProgressPercent.setText(getString(R.string.percent_format, percent));
        progressBar.setProgress(percent, true);
    }
}
