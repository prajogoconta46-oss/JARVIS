package com.jarvis.assistant

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = "jarvis_reminders"
        if (Build.VERSION.SDK_INT >= 26) manager.createNotificationChannel(NotificationChannel(channel, "Lembretes JARVIS", NotificationManager.IMPORTANCE_DEFAULT))
        val notification = if (Build.VERSION.SDK_INT >= 26) Notification.Builder(context, channel) else Notification.Builder(context)
        notification.setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle("JARVIS").setContentText(intent.getStringExtra("title") ?: "Lembrete").setAutoCancel(true)
        manager.notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notification.build())
    }
}