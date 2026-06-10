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

@Entity(tableName = "affiliate_offers")
data class AffiliateOfferEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val brandName: String,
    val commissionRate: String, // e.g., "30% per Purchase"
    val productUrl: String,
    val category: String, // SaaS, Tech, E-commerce, Retail, Design, Finance
    val description: String,
    val payoutInfo: String, // e.g., "Monthly, Min Payout ₹1,000"
    val isSaved: Boolean = false,
    val isApplied: Boolean = false,
    val affiliateLink: String = ""
)

@Entity(tableName = "affiliate_earnings")
data class AffiliateEarningEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val offerId: Int,
    val offerTitle: String,
    val brandName: String,
    val clicksCount: Int = 0,
    val conversionsCount: Int = 0,
    val totalSales: Double = 0.0,
    val earningsAmount: Double = 0.0, // Commission amount
    val status: String = "Pending", // "Pending", "Approved", "Paid"
    val timestamp: Long = System.currentTimeMillis()
)
