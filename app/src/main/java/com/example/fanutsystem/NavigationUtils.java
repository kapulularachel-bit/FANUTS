package com.example.fanutsystem;

import android.app.Activity;
import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationUtils {

    public static void setupBottomNavigation(Activity activity, int currentItemId) {
        BottomNavigationView bottomNav = activity.findViewById(R.id.bottom_navigation);
        if (bottomNav == null) {
            return;
        }

        bottomNav.setSelectedItemId(currentItemId);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            // Do nothing if the selected item is the current one
            if (itemId == currentItemId) {
                return true;
            }

            Intent intent = null;
            if (itemId == R.id.nav_home) {
                intent = new Intent(activity, MainActivity.class);
            } else if (itemId == R.id.nav_child) {
                intent = new Intent(activity, ChildModuleActivity.class);
            } else if (itemId == R.id.nav_community) {
                intent = new Intent(activity, CommunityActivity.class);
            } else if (itemId == R.id.nav_emergency) {
                intent = new Intent(activity, EmergencyActivity.class);
            }

            if (intent != null) {
                // Remove the stack of previous activities so we don't build a huge stack
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                // Also add no animation flag
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                
                activity.startActivity(intent);
                // Override transitions to disable animations
                activity.overridePendingTransition(0, 0);
            }
            return true;
        });
    }
}
