package com.jarvis.assistant

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotificationCaptureService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val pkg = sbn?.packageName ?: return
        if (pkg == packageName) return
        val preferences = getSharedPreferences("jarvis_notifications", MODE_PRIVATE)
        val nextCount = preferences.getInt("count", 0) + 1
        preferences.edit()
            .putInt("count", nextCount)
            .putString("last_package", pkg)
            .putLong("last_time", System.currentTimeMillis())
            .apply()
    }
}