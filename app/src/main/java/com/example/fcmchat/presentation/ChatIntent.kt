package com.example.fcmchat.presentation

import android.content.Context


sealed interface ChatIntent {
    data class SendMessage( val text: String) : ChatIntent
    data class UpdateMessageInput(val text: String) : ChatIntent
    object LoadFcmToken : ChatIntent
    object RequestNotificationPermission : ChatIntent
    object Logout : ChatIntent

}