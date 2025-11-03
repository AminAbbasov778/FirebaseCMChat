package com.example.fcmchat.domain.repository

import com.example.fcmchat.data.FCMessage

interface ChatRepository {
    suspend fun sendNotification(notification: FCMessage): Result<Unit>

}