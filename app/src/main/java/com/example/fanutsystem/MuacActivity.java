package com.example.fanutsystem;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class MuacActivity extends AppCompatActivity {

    private TextInputEditText etMuac;
    private TextView tvResult;
    private MaterialCardView resultCard;
    private ImageView ivStatusIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muac);

        // Setup Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Bind UI
        etMuac = findViewById(R.id.etMuac);
        tvResult = findViewById(R.id.tvMuacResult);
        resultCard = findViewById(R.id.resultCard);
        ivStatusIcon = findViewById(R.id.ivStatusIcon);
        MaterialButton btnCheck = findViewById(R.id.btnCheckMuac);

        if (btnCheck != null) {
            btnCheck.setOnClickListener(v -> {
                String input = etMuac.getText().toString().trim();

                if (input.isEmpty()) {
                    etMuac.setError(getString(R.string.error_all_values));
                    return;
                }

                try {
                    double muacValue = Double.parseDouble(input);
                    classifyAndDisplay(muacValue);
                } catch (NumberFormatException e) {
                    etMuac.setError(getString(R.string.error_invalid_numbers));
                }
            });
        }
    }

    private void classifyAndDisplay(double muac) {
        MuacClassification.MuacResult result = MuacClassification.classify(this, muac);
        tvResult.setText(result.statusLabel);
        tvResult.setTextColor(result.color);
        int color = result.color;
        
        // Update Icon color
        ivStatusIcon.setColorFilter(color);

        // Animation logic
        if (resultCard.getVisibility() == View.GONE) {
            resultCard.setVisibility(View.VISIBLE);
            Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            resultCard.startAnimation(fadeIn);
        } else {
            // Pulse animation if already visible
            Animation pulse = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            pulse.setDuration(500);
            resultCard.startAnimation(pulse);
        }
    }
}
