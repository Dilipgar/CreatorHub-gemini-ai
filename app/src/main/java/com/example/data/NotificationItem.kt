package com.example.data

data class NotificationItem(
    val id: Int,
    val title: String,
    val body: String,
    val timestamp: String,
    val type: String, // "deal", "campaign", "system", "profile"
    val isRead: Boolean = false
)
