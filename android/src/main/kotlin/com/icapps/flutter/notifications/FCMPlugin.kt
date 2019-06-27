package com.icapps.flutter.notifications

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.icapps.flutter.notifications.util.ActivityCounter
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.io.IOException
import java.util.*

class FCMPlugin private constructor(registrar: Registrar, private val channel: MethodChannel) : BroadcastReceiver(), MethodCallHandler {

    init {

        val intentFilter = IntentFilter()
        intentFilter.addAction(FCMReceiver.ACTION_TOKEN_FLUTTER)
        intentFilter.addAction(FCMReceiver.ACTION_ACTION_NOTIFICATION_FLUTTER)
        val manager = LocalBroadcastManager.getInstance(registrar.context())
        manager.registerReceiver(this, intentFilter)
    }

    // BroadcastReceiver implementation.
    override fun onReceive(context: Context, intent: Intent) {

        val action = intent.action ?: return

        if (action == FCMReceiver.ACTION_TOKEN_FLUTTER) {
            handleToken(intent)
        } else if (action == FCMReceiver.ACTION_ACTION_NOTIFICATION_FLUTTER) {
            handleRemoteMessage(intent)
        } else {
            throw RuntimeException("Not Implemented")
        }
    }

    private fun handleToken(intent: Intent) {

        val token = intent.getStringExtra(FCMService.EXTRA_TOKEN)
        channel.invokeMethod("onToken", token)
    }

    private fun handleRemoteMessage(intent: Intent) {

        val data = intent.getSerializableExtra(FCMService.EXTRA_NOTIFICATION) as HashMap<String, String>
        channel.invokeMethod("onMessage", data)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {

        when {
            "configure" == call.method -> {
                FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "getToken, error fetching instanceID: ", task.exception)
                        return@OnCompleteListener
                    }
                    channel.invokeMethod("onToken", task.result!!.token)
                })
                result.success(null)
            }
            "getToken" == call.method -> FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getToken, error fetching instanceID: ", task.exception)
                    result.success(null)
                    return@OnCompleteListener
                }

                result.success(task.result!!.token)
            })
            "deleteInstanceID" == call.method -> Thread(Runnable {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId()
                    result.success(true)
                } catch (ex: IOException) {
                    Log.e(TAG, "deleteInstanceID, error:", ex)
                    result.success(false)
                }
            }).start()
            "setBadge" == call.method -> {
                Log.d(TAG, "setBadge is not yet supported on android")
                result.success(false)
            }
            else -> result.notImplemented()
        }
    }

    companion object {

        private const val TAG = "FCMPlugin"

        @JvmStatic
        fun registerWith(registrar: Registrar) {

            val channel = MethodChannel(registrar.messenger(), "com.icapps.flutter/icapps_notifications")
            channel.setMethodCallHandler(FCMPlugin(registrar, channel))
        }

        @JvmStatic
        fun init(application: Application, enableDefaultBackgroundReceiver: Boolean = true) {

            application.registerActivityLifecycleCallbacks(ActivityCounter())
            FirebaseApp.initializeApp(application)
            FCMReceiver.register(application)
            if (enableDefaultBackgroundReceiver) {
                FCMDefaultBackgroundReceiver.register(application)
            }
        }
    }
}
