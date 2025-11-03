package com.example.fcmchat.domain.repository

interface FcmRepository {
    suspend fun sendMessage(
        targetToken: String,
        title: String,
        body: String
    ): Result<Unit>
}