package com.example.fanutsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Notification Channel
        NotificationHelper.createNotificationChannel(this);

        // Bind UI Elements
        View logoContainer = findViewById(R.id.logoContainer);
        View tvAppName = findViewById(R.id.tvAppName);
        View tvSubtitle = findViewById(R.id.tvSubtitle);
        View tvServicesLabel = findViewById(R.id.tvServicesLabel);
        View btnChild = findViewById(R.id.btnChildModuleCard);
        View btnCommunity = findViewById(R.id.btnCommunityModuleCard);
        View medicalCard = findViewById(R.id.medicalInfoCard);

        // Setup Bottom Navigation
        NavigationUtils.setupBottomNavigation(this, R.id.nav_home);

        // Apply smooth entrance animations
        startSmoothAnimator(logoContainer, 0);
        startSmoothAnimator(tvAppName, 50);
        startSmoothAnimator(tvSubtitle, 100);
        startSmoothAnimator(tvServicesLabel, 150);
        startSmoothAnimator(btnChild, 200);
        startSmoothAnimator(btnCommunity, 250);
        startSmoothAnimator(medicalCard, 300);

        // Navigation Logic for Cards
        if (btnChild != null) {
            btnChild.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, ChildModuleActivity.class));
            });
        }

        if (btnCommunity != null) {
            btnCommunity.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, CommunityActivity.class));
            });
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
}
