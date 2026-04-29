package com.example.fanutsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Notification Channel
        NotificationHelper.createNotificationChannel(this);

        // Bind UI Elements for Animation
        View logoContainer = findViewById(R.id.logoContainer);
        View tvAppName = findViewById(R.id.tvAppName);
        View tvSubtitle = findViewById(R.id.tvSubtitle);
        View medicalCard = findViewById(R.id.medicalInfoCard);
        View maternalCard = findViewById(R.id.maternalInfoCard);
        View selectLabel = findViewById(R.id.tvSelectLabel);
        View btnChild = findViewById(R.id.btnChildModuleCard);
        View btnCommunity = findViewById(R.id.btnCommunityModuleCard);
        View btnNotifications = findViewById(R.id.btnNotifications);

        // Setup Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_child) {
                startActivity(new Intent(this, ChildModuleActivity.class));
                return true;
            } else if (itemId == R.id.nav_community) {
                startActivity(new Intent(this, CommunityActivity.class));
                return true;
            } else if (itemId == R.id.nav_emergency) {
                startActivity(new Intent(this, EmergencyActivity.class));
                return true;
            }
            return itemId == R.id.nav_home;
        });

        // Create Custom Animations
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(800);

        Animation slideUp = new TranslateAnimation(0, 0, 50, 0);
        slideUp.setDuration(700);
        
        AnimationSet combined = new AnimationSet(true);
        combined.addAnimation(fadeIn);
        combined.addAnimation(slideUp);

        // Apply Entrance Animations with Staggered Delays
        startStaggeredAnimation(logoContainer, combined, 0);
        startStaggeredAnimation(tvAppName, fadeIn, 100);
        startStaggeredAnimation(tvSubtitle, fadeIn, 200);
        startStaggeredAnimation(selectLabel, fadeIn, 400);
        startStaggeredAnimation(btnChild, combined, 500);
        startStaggeredAnimation(btnCommunity, combined, 600);
        startStaggeredAnimation(medicalCard, combined, 700);
        startStaggeredAnimation(maternalCard, combined, 800);

        // Setup Notification Button
        if (btnNotifications != null) {
            btnNotifications.setOnClickListener(v -> {
                // TODO: Navigate to notifications or show info dialog
            });
        }

        // Navigation Logic for Cards (Keep these as well for intuitive tap)
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

    private void startStaggeredAnimation(View view, Animation anim, long delay) {
        if (view != null) {
            view.setVisibility(View.INVISIBLE);
            view.postDelayed(() -> {
                view.setVisibility(View.VISIBLE);
                view.startAnimation(anim);
            }, delay);
        }
    }
}
