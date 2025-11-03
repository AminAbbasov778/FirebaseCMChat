package com.example.fcmchat.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.fcmchat.MainActivity
import com.example.fcmchat.R
import com.example.fcmchat.domain.usecases.GetCurrentUserUseCase
import com.example.fcmchat.domain.usecases.SaveFcmTokenUseCase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FCMService() : FirebaseMessagingService() {

    @Inject
    lateinit var saveFcmTokenUseCase: SaveFcmTokenUseCase

    @Inject
    lateinit var getCurrentUserIdUseCase: GetCurrentUserUseCase


    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        private const val TAG = "ChatFCMService"
        private const val CHANNEL_ID = "chat_messages_channel"
        private const val CHANNEL_NAME = "Chat Messages"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        serviceScope.launch {
            val userId = "user_${getCurrentUserIdUseCase()}"
            saveFcmTokenUseCase(userId, token).fold(
                onSuccess = {  },
                onFailure = { }
            )
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)


        if (isAppInForeground()) {
            return
        }


        message.notification?.let {
            val title = it.title ?: "Yeni mesaj"
            val body = it.body ?: "Mesaj tapılmadı"
            showNotification(title, body)
        }

        message.data.isNotEmpty().let {
            Log.d(TAG, "Data payload: ${message.data}")
            val title = message.data["title"] ?: "Yeni mesaj"
            val body = message.data["body"] ?: message.data["message"] ?: "Mesaj tapılmadı"
            showNotification(title, body)
        }
    }

    private fun showNotification(title: String, body: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Mesaj bildirişləri üçün kanal"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_speech_balloon)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setColor(0xFF667EEA.toInt())
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
    private fun isAppInForeground(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = packageName
        return appProcesses.any {
            it.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    it.processName == packageName
        }
    }

}


