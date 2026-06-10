package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "opportunities")
data class OpportunityEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val brandName: String,
    val budgetRange: String,
    val type: String, // "Brand Deal", "Affiliate", "Collab", "Contest"
    val platform: String, // "YouTube", "Instagram", "TikTok", "Canva Pro"
    val location: String,
    val durationText: String, // "Ends in 5 days"
    val requirements: String,
    val aboutCampaign: String,
    val isSaved: Boolean = false,
    val commissionRate: String = "", // e.g., "Commission up to 40%"
    val difficultyLevel: String = "", // Beginner, Intermediate, Expert
    val category: String = "" // SaaS, Tech, E-commerce, Design
)

@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val partnerName: String, // name of brand or creator
    val platformType: String, // "YouTube", "Instagram", etc.
    val lastMessage: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chatSessionId: Int,
    val sender: String, // "creator" or "brand"
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "deals")
data class DealEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val opportunityId: Int,
    val title: String,
    val brandName: String,
    val dealAmount: String,
    val status: String, // "Applied", "Accepted", "Work Submitted", "Under Review", "Completed", "Payment Released"
    val timestamp: Long = System.currentTimeMillis()
)
