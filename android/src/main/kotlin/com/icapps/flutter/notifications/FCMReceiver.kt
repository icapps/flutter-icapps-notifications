package com.icapps.flutter.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.RemoteMessage
import com.icapps.flutter.notifications.util.ActivityCounter
import java.util.*

internal class FCMReceiver : BroadcastReceiver() {

    private val actionToken: String
        get() = if (ActivityCounter.amountOfActivities == 0) {
            ACTION_TOKEN
        } else ACTION_TOKEN_FLUTTER

    private val actionNotification: String
        get() = if (ActivityCounter.amountOfActivities == 0) {
            ACTION_NOTIFICATION
        } else ACTION_ACTION_NOTIFICATION_FLUTTER

    override fun onReceive(context: Context, intent: Intent) {

        val action = intent.action ?: return

        when (action) {
            FCMService.ACTION_TOKEN -> handleToken(context, intent)
            FCMService.ACTION_NOTIFICATION -> handleRemoteMessage(context, intent)
            else -> throw RuntimeException("Not Implemented")
        }
    }

    private fun handleToken(context: Context, intent: Intent) {

        val token = intent.getStringExtra(FCMService.EXTRA_TOKEN)
        val broadcastIntent = Intent(actionToken)
        broadcastIntent.putExtra(FCMService.EXTRA_TOKEN, token)
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent)
    }

    private fun handleRemoteMessage(context: Context, intent: Intent) {

        val message = intent.getParcelableExtra<RemoteMessage>(FCMService.EXTRA_NOTIFICATION)
        if (message.data == null) {
            Log.d(TAG, "Failed to get data from remote notification message")
            return
        }
        val data = HashMap(message.data)
        val broadcastIntent = Intent(actionNotification)
        broadcastIntent.putExtra(FCMService.EXTRA_NOTIFICATION, data)
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent)
    }

    companion object {

        private const val TAG = "FCMReceiver"

        const val ACTION_ACTION_NOTIFICATION_FLUTTER = "com.icapps.flutter.notifications.NOTIFICATION_FLUTTER"

        const val ACTION_NOTIFICATION = "com.icapps.flutter.notifications.NOTIFICATION"

        const val ACTION_TOKEN_FLUTTER = "com.icapps.flutter.notifications.TOKEN_FLUTTER"

        const val ACTION_TOKEN = "com.icapps.flutter.notifications.TOKEN"

        fun register(context: Context) {

            val intentFilter = IntentFilter()
            intentFilter.addAction(FCMService.ACTION_TOKEN)
            intentFilter.addAction(FCMService.ACTION_NOTIFICATION)
            val manager = LocalBroadcastManager.getInstance(context)
            manager.registerReceiver(FCMReceiver(), intentFilter)
        }
    }
}
