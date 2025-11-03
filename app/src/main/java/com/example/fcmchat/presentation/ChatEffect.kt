package com.example.fcmchat.presentation

sealed interface ChatEffect {
    data class ShowError(val message: String) : ChatEffect
    object MessageSent : ChatEffect
    object ScrollToBottom : ChatEffect
    data class ShowToast(val message: String) : ChatEffect
}
