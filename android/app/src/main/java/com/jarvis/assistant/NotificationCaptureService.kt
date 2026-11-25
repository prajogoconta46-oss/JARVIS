package com.jarvis.assistant

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotificationCaptureService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val pkg = sbn?.packageName ?: return
        if (pkg == packageName) return
        getSharedPreferences("jarvis_notifications", MODE_PRIVATE).edit()
            .putInt("count", getSharedPreferences("jarvis_notifications", MODE_PRIVATE).getInt("count", 0) + 1)
            .putString("last_package", pkg)
            .putLong("last_time", System.currentTimeMillis())
            .apply()
    }
}