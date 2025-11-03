package com.example.fcmchat.data
import android.content.Context
import android.util.Log
import com.example.fcmchat.domain.repository.FcmRepository
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import javax.inject.Inject

class FCMRepositoryImpl @Inject constructor(
    private val context: Context,
    private val api: FcmApi
) : FcmRepository{

    companion object {
        private const val SCOPE = "https://www.googleapis.com/auth/firebase.messaging"
    }

   override suspend fun sendMessage(targetToken: String, title: String, body: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val accessToken = getAccessToken()

                val request = FcmMessageRequest(
                    message = FcmMessageRequest.Message(
                        token = targetToken,
                        notification = FcmMessageRequest.Notification(
                            title = title,
                            body = body
                        ),
                        data = mapOf("sender" to "TestUser")
                    )
                )

                val response = api.sendMessage("Bearer $accessToken", request)
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("FCM send failed: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun getAccessToken(): String {
        val stream: InputStream = context.resources.openRawResource(
            context.resources.getIdentifier("service_account", "raw", context.packageName)
        )
        val credentials = GoogleCredentials.fromStream(stream)
            .createScoped(listOf(SCOPE))
        credentials.refreshIfExpired()
        return credentials.accessToken.tokenValue
    }
}
