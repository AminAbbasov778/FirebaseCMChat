package com.example.fcmchat.presentation


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fcmchat.data.FCMessage
import com.example.fcmchat.data.NotificationData
import com.example.fcmchat.domain.usecases.GetAllTokensUseCase
import com.example.fcmchat.domain.usecases.GetCurrentUserUseCase
import com.example.fcmchat.domain.usecases.GetFcmTokenUseCase
import com.example.fcmchat.domain.usecases.GetMessagesUseCase
import com.example.fcmchat.domain.usecases.LogoutUseCase
import com.example.fcmchat.domain.usecases.SaveFcmTokenUseCase
import com.example.fcmchat.domain.usecases.SendMessageUseCase
import com.example.fcmchat.domain.usecases.SendNotificationUseCase
import com.example.fcmchat.domain.usecases.SignInAnonymouslyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject






@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getFcmTokenUseCase: GetFcmTokenUseCase,
    private val saveFcmTokenUseCase: SaveFcmTokenUseCase,
    private val getAllTokensUseCase: GetAllTokensUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserUseCase,
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val sendNotificationUseCase: SendNotificationUseCase


) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    private val _effect = Channel<ChatEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
       getCurrentUserId()


    }







    fun logout(){
        logoutUseCase()
        _state.update {
            it.copy(
                currentUserId = null,
                username = null,
                fcmToken = null,
                isBack = true
            )
        }
    }


    fun handleIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.SendMessage -> sendMessage(intent.text)
            is ChatIntent.UpdateMessageInput -> updateMessageInput(intent.text)
            is ChatIntent.LoadFcmToken -> loadFcmToken()
            is ChatIntent.RequestNotificationPermission -> {}
            ChatIntent.Logout -> logout()
        }
    }

    private fun observeMessages() {
        viewModelScope.launch {
            getMessagesUseCase().collect { messages ->
                _state.update { it.copy(messages = messages) }
            }
        }
    }

    fun getCurrentUserId() {
        val id = getCurrentUserIdUseCase()
        if (id != null) {
            _state.update { it.copy(currentUserId = id, username = "user_$id") }
            observeMessages()

        } else {
            viewModelScope.launch {
                signInAnonymouslyUseCase().fold(
                    onSuccess = { newUser ->
                        observeMessages()
                        _state.update {
                            it.copy(currentUserId = newUser, username = "user_${newUser}")
                        }
                        loadFcmToken()
                    },
                    onFailure = { Log.e("ChatView", "Anonymous login xətası: ${it.message}") }
                )
            }
        }
    }

    private fun loadFcmToken() {
        val userId = _state.value.currentUserId ?: return
        viewModelScope.launch {
            getFcmTokenUseCase().fold(
                onSuccess = { token ->
                    _state.update { it.copy(fcmToken = token) }
                    saveFcmTokenUseCase(userId, token)
                    _effect.send(ChatEffect.ShowToast("FCM Token is uploaded"))
                },
                onFailure = { error ->
                    _effect.send(ChatEffect.ShowError("Token alına bilmədi"))
                }
            )
        }
    }

    private fun sendMessage(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }


            launch {
                sendMessageUseCase(
                    text = text,
                    senderId = _state.value.currentUserId ?: "",
                    senderName = _state.value.username ?: "",
                    token = _state.value.fcmToken ?: ""
                ).fold(
                    onSuccess = {
                        _state.update {
                            it.copy(
                                messageInput = "",
                                isLoading = false
                            )
                        }
                        _effect.send(ChatEffect.MessageSent)
                        _effect.send(ChatEffect.ScrollToBottom)
                    },
                    onFailure = { error ->
                        _state.update { it.copy(isLoading = false) }
                        _effect.send(ChatEffect.ShowError(error.message ?: "Mesaj göndərilə bilmədi"))
                    }
                )
            }

            launch {
                getAllTokensUseCase().fold(
                    onSuccess = { tokens ->
                        tokens.forEach { userToken ->
                            if(userToken.userId != _state.value.currentUserId){
                                viewModelScope.async {
                                    sendNotificationUseCase(userToken.token,_state.value.username ?: "",text)
                                }.await()
                            }


                        }
                    },
                    onFailure = { Log.e("ChatViewModel", "Failed to get tokens: ${it.message}") }
                )

            }





        }
    }

    private fun updateMessageInput(text: String) {
        _state.update { it.copy(messageInput = text) }
    }
}