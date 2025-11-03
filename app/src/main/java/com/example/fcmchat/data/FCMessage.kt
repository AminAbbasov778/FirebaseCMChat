package com.example.fcmchat.data

data class FCMessage(val  to : String,val notification : NotificationData,val data: Map<String, String>? = null)
