package com.example.fanutsystem;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class ChildDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_details);

        // Get the child object from intent using version-safe method
        Child child;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            child = getIntent().getSerializableExtra("child_data", Child.class);
        } else {
            //noinspection deprecation
            child = (Child) getIntent().getSerializableExtra("child_data");
        }

        if (child != null) {
            TextView tvName = findViewById(R.id.tvDetailName);
            TextView tvGender = findViewById(R.id.tvDetailGender);
            TextView tvDob = findViewById(R.id.tvDetailDob);
            TextView tvWeight = findViewById(R.id.tvDetailWeight);
            TextView tvHeight = findViewById(R.id.tvDetailHeight);
            TextView tvMuac = findViewById(R.id.tvDetailMuac);

            tvName.setText(child.getName());
            tvGender.setText(getString(R.string.label_detail_gender, child.getGender()));
            tvDob.setText(getString(R.string.label_detail_dob, child.getDob()));
            tvWeight.setText(getString(R.string.label_detail_weight, child.getWeight()));
            tvHeight.setText(getString(R.string.label_detail_height, child.getHeight()));
            tvMuac.setText(getString(R.string.label_detail_muac, child.getMuac()));

            MaterialButton btnAnalyze = findViewById(R.id.btnAnalyzeGrowth);
            if (btnAnalyze != null) {
                btnAnalyze.setOnClickListener(v -> {
                    Intent intent = new Intent(this, GrowthMonitoringActivity.class);
                    // Pass current child metrics to analysis module
                    intent.putExtra("child_weight", child.getWeight());
                    intent.putExtra("child_height", child.getHeight());
                    intent.putExtra("child_muac", child.getMuac());
                    intent.putExtra("child_dob", child.getDob());
                    startActivity(intent);
                });
            }
        }

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}
