package com.example.fcmchat.domain.repository

import com.example.fcmchat.data.UserToken
import com.example.fcmchat.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {

    fun getMessages(): Flow<List<Message>>
    suspend fun sendMessage(message: Message): Result<Unit>
    suspend fun saveFcmToken(userId: String, token: String): Result<Unit>
    suspend fun getFcmToken(): Result<String>
    suspend fun getUserTokens(): Result<List<UserToken>>
   fun  getCurrentUserId(): String?
   suspend fun signInAnonymously(): Result<String>
    fun logout()

}