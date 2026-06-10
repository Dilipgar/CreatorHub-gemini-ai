package com.example.data

import kotlinx.coroutines.flow.Flow

class CreatorHubRepository(private val db: AppDatabase) {
    val opportunities: Flow<List<OpportunityEntity>> = db.opportunityDao().getAllOpportunities()
    val chatSessions: Flow<List<ChatSessionEntity>> = db.chatDao().getAllSessions()
    val activeDeals: Flow<List<DealEntity>> = db.dealDao().getAllDeals()

    fun getOpportunity(id: Int): Flow<OpportunityEntity?> {
        return db.opportunityDao().getOpportunityById(id)
    }

    suspend fun insertOpportunity(op: OpportunityEntity): Long {
        return db.opportunityDao().insertOpportunity(op)
    }

    suspend fun toggleSaveOpportunity(opId: Int, isSaved: Boolean) {
        // Direct Flow helper is a bit complex, let's fetch first or do it via direct query if needed.
        // For simplicity, we can pass the entity or modify it. Let's do a fast implementation by letting the DAO update.
    }

    suspend fun updateOpportunity(op: OpportunityEntity) {
        db.opportunityDao().updateOpportunity(op)
    }

    // Messages
    fun getMessages(sessionId: Int): Flow<List<MessageEntity>> {
        return db.chatDao().getMessagesForSession(sessionId)
    }

    suspend fun sendMessage(sessionId: Int, messageText: String, sender: String) {
        val now = System.currentTimeMillis()
        val msg = MessageEntity(
            chatSessionId = sessionId,
            sender = sender,
            messageText = messageText,
            timestamp = now
        )
        db.chatDao().insertMessage(msg)
        db.chatDao().updateSessionLastMessage(sessionId, messageText, now)
    }

    suspend fun startChatSession(partnerName: String, platform: String, initialMsg: String): Long {
        val now = System.currentTimeMillis()
        val sId = db.chatDao().insertSession(
            ChatSessionEntity(
                partnerName = partnerName,
                platformType = platform,
                lastMessage = initialMsg,
                timestamp = now
            )
        )
        val msg = MessageEntity(
            chatSessionId = sId.toInt(),
            sender = "creator",
            messageText = initialMsg,
            timestamp = now
        )
        db.chatDao().insertMessage(msg)
        return sId
    }

    // Deals
    fun getDealStateForOpportunity(opId: Int): Flow<DealEntity?> {
        return db.dealDao().getDealByOpportunityId(opId)
    }

    suspend fun applyToOpportunity(opId: Int, title: String, brandName: String, proposedAmount: String) {
        val existing = db.dealDao().getDealByOpportunityIdDirect(opId)
        if (existing == null) {
            val deal = DealEntity(
                opportunityId = opId,
                title = title,
                brandName = brandName,
                dealAmount = proposedAmount,
                status = "Applied",
                timestamp = System.currentTimeMillis()
            )
            db.dealDao().insertDeal(deal)
        }
    }

    suspend fun updateDealStatus(dealId: Int, status: String) {
        db.dealDao().updateDealStatus(dealId, status, System.currentTimeMillis())
    }
}
