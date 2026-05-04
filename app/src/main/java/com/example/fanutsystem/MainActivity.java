package com.example.fanutsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Notification Channel
        NotificationHelper.createNotificationChannel(this);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            NotificationMenuHelper.attach(this, toolbar);
        }

        View landingHero = findViewById(R.id.landingHero);
        View labelPrimary = findViewById(R.id.tvSelectLabel);
        View sectionNextAction = findViewById(R.id.sectionNextAction);
        View btnQuickVaccination = findViewById(R.id.btnQuickVaccination);
        View labelQuickActions = findViewById(R.id.labelQuickActions);
        View gridQuickActions = findViewById(R.id.gridQuickActions);
        View bottomNav = findViewById(R.id.bottom_navigation);
        MaterialButton btnLangEnglish = findViewById(R.id.btnLangEnglish);
        MaterialButton btnLangChichewa = findViewById(R.id.btnLangChichewa);

        NavigationUtils.setupBottomNavigation(this, R.id.nav_home);
        setupLanguageSwitcher(btnLangEnglish, btnLangChichewa);

        startSmoothAnimator(toolbar, 0);
        startSmoothAnimator(landingHero, 40);
        startSmoothAnimator(labelPrimary, 70);
        startSmoothAnimator(sectionNextAction, 120);
        startSmoothAnimator(btnQuickVaccination, 170);
        startSmoothAnimator(labelQuickActions, 220);
        startSmoothAnimator(gridQuickActions, 270);
        startSmoothAnimator(bottomNav, 320);

        // Setup Click Listeners for Quick Actions
        View btnMeasurement = findViewById(R.id.btnChildModuleCard);
        if (btnMeasurement != null) {
            btnMeasurement.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ChildModuleActivity.class)));
        }

        if (btnQuickVaccination != null) {
            btnQuickVaccination.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, VaccinationActivity.class)));
        }

        View btnCommunity = findViewById(R.id.btnCommunityModuleCard);
        if (btnCommunity != null) {
            btnCommunity.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CommunityActivity.class)));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (toolbar != null) {
            NotificationMenuHelper.refreshBadge(this, toolbar);
        }
    }

    private void startSmoothAnimator(View view, long delay) {
        if (view != null) {
            view.setAlpha(0f);
            view.setTranslationY(60f);
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay(delay)
                .setInterpolator(new DecelerateInterpolator(1.5f))
                .start();
        }
    }

    private void setupLanguageSwitcher(MaterialButton btnEnglish, MaterialButton btnChichewa) {
        if (btnEnglish == null || btnChichewa == null) {
            return;
        }

        refreshLanguageButtons(btnEnglish, btnChichewa);

        btnEnglish.setOnClickListener(v -> applyLanguage("en"));
        btnChichewa.setOnClickListener(v -> applyLanguage("ny"));
    }

    private void applyLanguage(String languageTag) {
        String current = AppCompatDelegate.getApplicationLocales().toLanguageTags();
        if (languageTag.equalsIgnoreCase(current)) {
            return;
        }
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag));
    }

    private void refreshLanguageButtons(MaterialButton btnEnglish, MaterialButton btnChichewa) {
        String current = AppCompatDelegate.getApplicationLocales().toLanguageTags();
        if (current == null || current.isEmpty()) {
            current = LocaleListCompat.getDefault().toLanguageTags();
        }
        boolean chichewa = current != null && current.toLowerCase().startsWith("ny");
        btnEnglish.setAlpha(chichewa ? 0.65f : 1f);
        btnChichewa.setAlpha(chichewa ? 1f : 0.65f);
    }
}
