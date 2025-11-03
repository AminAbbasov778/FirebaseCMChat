package com.example.fcmchat.data

import android.util.Log
import com.example.fcmchat.domain.model.Message
import com.example.fcmchat.domain.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseMessaging: FirebaseMessaging,
    private val firebaseAuth: FirebaseAuth
) : FirebaseRepository {

    private val messagesCollection = firestore.collection("messages")
    private val tokensCollection = firestore.collection("fcm_tokens")

    override fun getMessages(): Flow<List<Message>> = callbackFlow {
        val subscription = messagesCollection
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Message(
                            id = doc.id,
                            text = doc.getString("text") ?: "",
                            senderId = doc.getString("senderId") ?: "",
                            senderName = doc.getString("senderName") ?: "",
                            timestamp = doc.getLong("timestamp") ?: 0L,
                            isRead = doc.getBoolean("isRead") ?: false
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(messages)
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun sendMessage(message: Message): Result<Unit> {
        return try {
            messagesCollection.document(message.id).set(
                hashMapOf(
                    "text" to message.text,
                    "senderId" to message.senderId,
                    "senderName" to message.senderName,
                    "timestamp" to message.timestamp,
                    "isRead" to false,

                )
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveFcmToken(userId: String, token: String): Result<Unit> {
        return try {
            tokensCollection.document(userId).set(
                hashMapOf(
                    "token" to token,
                    "userId" to userId,
                    "timestamp" to System.currentTimeMillis()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }




    override suspend fun getUserTokens(): Result<List<UserToken>> {
        return try {
            val tokens = tokensCollection.get().await()
            Result.success(
                tokens.documents.mapNotNull { doc ->
                    val userId = doc.getString("userId")
                    val token = doc.getString("token")
                    if (userId != null && token != null) UserToken(userId, token) else null
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFcmToken(): Result<String> {
        return try {
            val token = firebaseMessaging.token.await()
            Result.success(token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

  override  suspend fun signInAnonymously(): Result<String> {
        return try {
            val result = firebaseAuth.signInAnonymously().await()
            val uid = result.user?.uid ?: throw Exception("User id is null")

            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }




   override fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

    override fun logout() {
        firebaseAuth.signOut()

    }

}
