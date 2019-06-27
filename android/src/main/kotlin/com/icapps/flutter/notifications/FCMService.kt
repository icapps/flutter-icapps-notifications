package com.icapps.flutter.notifications

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        val intent = Intent(ACTION_NOTIFICATION)
        intent.putExtra(EXTRA_NOTIFICATION, remoteMessage)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    /**
     * Called when a new token for the default Firebase project is generated.
     *
     * @param token The token used for sending messages to this application instance. This token is
     * the same as the one retrieved by getInstanceId().
     */
    override fun onNewToken(token: String?) {

        val intent = Intent(ACTION_TOKEN)
        intent.putExtra(EXTRA_TOKEN, token)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    companion object {

        const val ACTION_NOTIFICATION = "com.icapps.flutter.notifications.NOTIFICATION_INTERNAL"

        const val EXTRA_NOTIFICATION = "notification"

        const val ACTION_TOKEN = "com.icapps.flutter.notifications.TOKEN_INTERNAL"

        const val EXTRA_TOKEN = "token"
    }
}
