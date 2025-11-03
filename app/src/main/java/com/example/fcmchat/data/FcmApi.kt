package com.example.fcmchat.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
interface FcmApi {
    @POST("projects/fcmchat-8533a/messages:send")
    suspend fun sendMessage(
        @Header("Authorization") authHeader: String,
        @Body message: FcmMessageRequest
    ): Response<Unit>
}
