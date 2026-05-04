package com.example.fanutsystem;

import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;

/**
 * Toolbar notification affordance aligned with Material (24dp glyph, themed {@code colorControlNormal}).
 */
public final class NotificationMenuHelper {

    private NotificationMenuHelper() {}

    public static void attach(AppCompatActivity activity, MaterialToolbar toolbar) {
        toolbar.inflateMenu(R.menu.menu_notifications);

        toolbar.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.action_notifications) {
                AlertsReminderBottomSheet.show(activity);
                return true;
            }
            return false;
        });

        refreshBadge(activity, toolbar);
    }

    public static void refreshBadge(AppCompatActivity activity, MaterialToolbar toolbar) {
        MenuItem notificationsItem = toolbar.getMenu().findItem(R.id.action_notifications);
        if (notificationsItem == null) {
            return;
        }
        Object existing = toolbar.getTag(R.id.action_notifications);
        if (existing instanceof BadgeDrawable) {
            BadgeUtils.detachBadgeDrawable((BadgeDrawable) existing, toolbar, R.id.action_notifications);
            toolbar.setTag(R.id.action_notifications, null);
        }

        int count = InAppReminderAggregator.reminderBadgeCount(activity);
        if (count <= 0) {
            return;
        }

        BadgeDrawable badge = BadgeDrawable.create(activity);
        badge.setVisible(true);
        badge.setNumber(Math.min(count, 99));
        badge.setBackgroundColor(ContextCompat.getColor(activity, R.color.status_danger));
        badge.setBadgeTextColor(ContextCompat.getColor(activity, R.color.white));
        BadgeUtils.attachBadgeDrawable(badge, toolbar, R.id.action_notifications);
        toolbar.setTag(R.id.action_notifications, badge);
    }
}
