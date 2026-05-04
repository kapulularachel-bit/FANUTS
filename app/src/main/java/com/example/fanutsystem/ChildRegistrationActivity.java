package com.example.fanutsystem;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.widget.TooltipCompat;

import java.util.Calendar;

public class ChildRegistrationActivity extends AppCompatActivity {

    // UI Elements
    private EditText nameInput, dobInput, weightInput, heightInput, muacInput;
    private MaterialButtonToggleGroup genderToggleGroup;
    private TextInputLayout layoutName, layoutDob, layoutWeight, layoutHeight, layoutMuac;
    private MaterialButton btnRegister;
    private ProgressBar progressLoading;
    private ImageView ivSuccess;
    private ImageButton btnMuacInfo;

    private View cardChildInfo, cardHealthMetrics, bottomBar, scrollContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_registration);

        // Initialize UI elements
        nameInput = findViewById(R.id.ChildName);
        dobInput = findViewById(R.id.DOb);
        weightInput = findViewById(R.id.Weight);
        heightInput = findViewById(R.id.Height);
        muacInput = findViewById(R.id.MUAC);
        genderToggleGroup = findViewById(R.id.genderToggleGroup);

        layoutName = findViewById(R.id.layoutChildName);
        layoutDob = findViewById(R.id.layoutDob);
        layoutWeight = findViewById(R.id.layoutWeight);
        layoutHeight = findViewById(R.id.layoutHeight);
        layoutMuac = findViewById(R.id.layoutMuac);

        btnRegister = findViewById(R.id.btnRegister);
        progressLoading = findViewById(R.id.progressLoading);
        ivSuccess = findViewById(R.id.ivSuccess);
        btnMuacInfo = findViewById(R.id.btnMuacInfo);

        cardChildInfo = findViewById(R.id.cardChildInfo);
        cardHealthMetrics = findViewById(R.id.cardHealthMetrics);
        bottomBar = findViewById(R.id.bottomBar);
        scrollContent = findViewById(R.id.scrollContent);

        ImageButton btnBack = findViewById(R.id.btnBack);

        // Setup Back Button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Setup Date Picker
        dobInput.setOnClickListener(v -> showDatePicker());

        // Setup MUAC Info Tooltip
        setupMuacTooltip();

        // Setup Gender Toggle with Animations and Selection Changes
        genderToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                View button = findViewById(checkedId);
                if (button != null) {
                    animateGenderSelection(button);
                    hapticFeedback(button);
                }
            }
        });

        // Setup Focus Animations for all inputs
        setupFocusAnimation(nameInput);
        setupFocusAnimation(dobInput);
        setupFocusAnimation(weightInput);
        setupFocusAnimation(heightInput);
        setupFocusAnimation(muacInput);

        // Run entry animations
        runEntryAnimations();

        // Register button click listener
        btnRegister.setOnClickListener(v -> {
            if (validateInputs()) {
                startSubmitAnimation();
            }
        });

        // Set minimum touch target sizes for accessibility
        setMinimumTouchTargets();
    }

    /**
     * Run staggered entry animations for sections
     */
    private void runEntryAnimations() {
        // Fade and slide up Child Info card
        cardChildInfo.setAlpha(0f);
        cardChildInfo.setTranslationY(80f);
        AnimatorSet childInfoAnim = createFadeSlideAnimator(cardChildInfo, 0, 0, 400, 0);

        // Fade and slide up Health Metrics card
        cardHealthMetrics.setAlpha(0f);
        cardHealthMetrics.setTranslationY(80f);
        AnimatorSet healthMetricsAnim = createFadeSlideAnimator(cardHealthMetrics, 0, 0, 400, 120);

        // Fade and slide up Bottom Bar
        bottomBar.setAlpha(0f);
        bottomBar.setTranslationY(80f);
        AnimatorSet bottomBarAnim = createFadeSlideAnimator(bottomBar, 0, 0, 400, 240);

        AnimatorSet masterSet = new AnimatorSet();
        masterSet.playTogether(childInfoAnim, healthMetricsAnim, bottomBarAnim);
        masterSet.start();
    }

    /**
     * Create fade and slide up animator
     */
    private AnimatorSet createFadeSlideAnimator(View view, float fromAlpha, float toAlpha,
                                                 long duration, long delay) {
        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(view, "alpha", fromAlpha, 1f);
        ObjectAnimator slideAnim = ObjectAnimator.ofFloat(view, "translationY", view.getTranslationY(), 0f);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(fadeAnim, slideAnim);
        set.setDuration(duration);
        set.setStartDelay(delay);
        set.setInterpolator(new DecelerateInterpolator(1.2f));

        return set;
    }

    /**
     * Setup focus animations with scale and color change
     */
    private void setupFocusAnimation(View view) {
        view.setOnFocusChangeListener((v, hasFocus) -> {
            float scale = hasFocus ? 1.02f : 1.0f;
            view.animate()
                    .scaleX(scale)
                    .scaleY(scale)
                    .setDuration(250)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();

            // Optional: Add highlight to parent TextInputLayout
            if (v.getParent() instanceof TextInputLayout && hasFocus) {
                hapticFeedback(v);
            }
        });
    }

    /**
     * Animate gender selection with bounce effect
     */
    private void animateGenderSelection(View view) {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.12f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.12f, 1f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY);
        animator.setDuration(350);
        animator.setInterpolator(new OvershootInterpolator(1.5f));
        animator.start();
    }

    /**
     * Subtle shake animation for validation errors
     */
    private void shakeAnimation(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX",
                0, 15, -15, 15, -15, 8, -8, 0);
        animator.setDuration(400);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    /**
     * Success checkmark animation
     */
    private void successCheckAnimation(View view) {
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.setAlpha(0f);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1.2f, 1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY, alpha);
        set.setDuration(350);
        set.setInterpolator(new OvershootInterpolator(1.5f));
        set.start();
    }

    /**
     * Show date picker dialog
     */
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedYear + "-" + String.format("%02d", (selectedMonth + 1)) +
                            "-" + String.format("%02d", selectedDay);
                    dobInput.setText(date);
                    layoutDob.setError(null);
                },
                year, month, day);

        datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePicker.show();
    }

    /**
     * Setup MUAC info tooltip
     */
    private void setupMuacTooltip() {
        String muacInfo = getString(R.string.muac_info_tooltip);
        TooltipCompat.setTooltipText(btnMuacInfo, muacInfo);

        btnMuacInfo.setOnClickListener(v -> {
            Toast.makeText(this, muacInfo, Toast.LENGTH_LONG).show();
            hapticFeedback(v);
        });
    }

    /**
     * Validate all form inputs
     */
    private boolean validateInputs() {
        boolean isValid = true;

        // Reset all errors
        layoutName.setError(null);
        layoutDob.setError(null);
        layoutWeight.setError(null);
        layoutHeight.setError(null);
        layoutMuac.setError(null);

        // Validate name
        String name = nameInput.getText().toString().trim();
        if (name.isEmpty()) {
            layoutName.setError(getString(R.string.error_name_required));
            shakeAnimation(layoutName);
            isValid = false;
        } else if (name.length() < 2) {
            layoutName.setError(getString(R.string.error_name_too_short));
            shakeAnimation(layoutName);
            isValid = false;
        }

        // Validate DOB
        String dob = dobInput.getText().toString().trim();
        if (dob.isEmpty()) {
            layoutDob.setError(getString(R.string.error_dob_required));
            shakeAnimation(layoutDob);
            isValid = false;
        }

        // Validate gender
        if (genderToggleGroup.getCheckedButtonId() == View.NO_ID) {
            Toast.makeText(this, getString(R.string.error_gender_required), Toast.LENGTH_SHORT).show();
            shakeAnimation(genderToggleGroup);
            isValid = false;
        }

        return isValid;
    }

    /**
     * Start submit animation with loading state
     */
    private void startSubmitAnimation() {
        // Disable button and show loading spinner
        btnRegister.setEnabled(false);
        btnRegister.setText("");
        progressLoading.setVisibility(View.VISIBLE);

        // Add ripple effect to button
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnRegister.setPressed(true);
            new Handler(Looper.getMainLooper()).postDelayed(
                    () -> btnRegister.setPressed(false), 100);
        }

        // Simulate network delay, then show success
        new Handler(Looper.getMainLooper()).postDelayed(
                this::saveDataAndShowSuccess, 1800);
    }

    /**
     * Save data to local storage and show success state
     */
    private void saveDataAndShowSuccess() {
        // Read form values
        String name = nameInput.getText().toString().trim();
        String dob = dobInput.getText().toString().trim();
        String weight = weightInput.getText().toString().trim();
        String height = heightInput.getText().toString().trim();
        String muac = muacInput.getText().toString().trim();

        // Get gender
        String gender = "Other";
        int checkedId = genderToggleGroup.getCheckedButtonId();
        if (checkedId == R.id.btnMale) {
            gender = getString(R.string.gender_male);
        } else if (checkedId == R.id.btnFemale) {
            gender = getString(R.string.gender_female);
        } else if (checkedId == R.id.btnOther) {
            gender = getString(R.string.gender_other);
        }

        // Create child object
        Child newChild = new Child(name, dob, gender, muac, weight, height);

        // Save to local storage
        ChildStorage.saveChild(this, newChild);

        // Update UI to success state
        progressLoading.setVisibility(View.GONE);
        ivSuccess.setVisibility(View.VISIBLE);
        successCheckAnimation(ivSuccess);

        // Change button background to success color
        ColorStateList successColor = ColorStateList.valueOf(
                getResources().getColor(R.color.status_success, getTheme()));
        btnRegister.setBackgroundTintList(successColor);

        hapticFeedback(btnRegister);

        // Show success message and navigate
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String successMsg = getString(R.string.registration_success) + ": " + name;
            Toast.makeText(ChildRegistrationActivity.this, successMsg, Toast.LENGTH_LONG).show();

            // Finish with animation
            finishWithAnimation();
        }, 1200);
    }

    /**
     * Finish activity with fade-out animation
     */
    private void finishWithAnimation() {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(scrollContent, "alpha", 1f, 0f);
        fadeOut.setDuration(300);
        fadeOut.setInterpolator(new DecelerateInterpolator());
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ChildRegistrationActivity.this.finish();
                overridePendingTransition(0, android.R.anim.fade_out);
            }
        });
        fadeOut.start();
    }

    /**
     * Haptic feedback for user interaction
     */
    private void hapticFeedback(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.performHapticFeedback(
                    android.view.HapticFeedbackConstants.VIRTUAL_KEY,
                    android.view.HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            );
        }
    }

    /**
     * Set minimum touch target sizes for accessibility (48dp minimum)
     */
    private void setMinimumTouchTargets() {
        int minSize = dpToPx(48);

        btnRegister.setMinimumHeight(minSize);
        btnRegister.setMinimumWidth(getResources().getDisplayMetrics().widthPixels);

        ImageButton backBtn = findViewById(R.id.btnBack);
        if (backBtn != null) {
            backBtn.setMinimumHeight(minSize);
            backBtn.setMinimumWidth(minSize);
        }

        if (btnMuacInfo != null) {
            btnMuacInfo.setMinimumHeight(minSize);
            btnMuacInfo.setMinimumWidth(minSize);
        }
    }

    /**
     * Utility to convert dp to pixels
     */
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
