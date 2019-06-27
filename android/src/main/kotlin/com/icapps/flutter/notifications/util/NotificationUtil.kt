package com.icapps.flutter.notifications.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationUtil {

    private val CHANNEL_ID = "General-Channel-Id"
    private val CHANNEL_NAME = "General"
    private val CHANNEL_DESCRIPTION = "General channel for receiving push notifications"

    fun createNotification(context: Context, intent: PendingIntent?, title: String, message: String, notificationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)

        if (intent == null || notificationManager == null) {
            return
        }
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(getAppIcon(context))
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setShowWhen(true)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setContentTitle(title)
                .setContentText(message)
                .setTicker(message)
                .setContentIntent(intent)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))

        val manager = notificationManager as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(manager)
        }

        manager.notify(notificationId, builder.build())
    }

    private fun getAppIcon(context: Context): Int {
        val applicationInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
        return applicationInfo.icon
    }

    private fun createNotificationChannel(manager: NotificationManager): String {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = CHANNEL_DESCRIPTION
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.setShowBadge(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

            manager.createNotificationChannel(channel)
            return CHANNEL_ID
        }
        return ""
    }
}