package com.icapps.flutter.notifications

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.icapps.flutter.notifications.util.NotificationUtil
import java.util.*

internal class FCMDefaultBackgroundReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        when (intent.action ?: return) {
            FCMReceiver.ACTION_TOKEN -> handleToken(context, intent)
            FCMReceiver.ACTION_NOTIFICATION -> handleRemoteMessage(context, intent)
            else -> throw RuntimeException("Not Implemented")
        }
    }

    private fun handleToken(context: Context, intent: Intent) {

        val token = intent.getStringExtra(FCMService.EXTRA_TOKEN)
        //does nothing with the tokens in the background.
    }

    private fun handleRemoteMessage(context: Context, intent: Intent) {

        val data = intent.getSerializableExtra(FCMService.EXTRA_NOTIFICATION) as HashMap<String, String>
        val title = data["title"]
        val message = data["message"]
        if (title == null || message == null) {
            Log.d(TAG, "'title' or 'message' was not provided")
            return
        }
        NotificationUtil.createNotification(context, getLauncherIntent(context), title, message, Random().nextInt())
    }

    private fun getLauncherIntent(context: Context): PendingIntent? {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        return PendingIntent.getActivity(context, 0, intent, 0)
    }

    companion object {

        private val TAG = "FCMDefaultReceiver"

        fun register(context: Context) {

            val intentFilter = IntentFilter()
            intentFilter.addAction(FCMReceiver.ACTION_TOKEN)
            intentFilter.addAction(FCMReceiver.ACTION_NOTIFICATION)
            val manager = LocalBroadcastManager.getInstance(context)
            manager.registerReceiver(FCMDefaultBackgroundReceiver(), intentFilter)
        }
    }
}
