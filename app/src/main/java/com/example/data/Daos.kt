package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OpportunityDao {
    @Query("SELECT * FROM opportunities ORDER BY id DESC")
    fun getAllOpportunities(): Flow<List<OpportunityEntity>>

    @Query("SELECT * FROM opportunities WHERE id = :id")
    fun getOpportunityById(id: Int): Flow<OpportunityEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOpportunity(op: OpportunityEntity): Long

    @Update
    suspend fun updateOpportunity(op: OpportunityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ops: List<OpportunityEntity>)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<ChatSessionEntity>>

    @Query("SELECT * FROM messages WHERE chatSessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSession(sessionId: Int): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChatSessionEntity): Long

    @Query("UPDATE chat_sessions SET lastMessage = :lastMsg, timestamp = :time WHERE id = :sessionId")
    suspend fun updateSessionLastMessage(sessionId: Int, lastMsg: String, time: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(msg: MessageEntity)
}

@Dao
interface DealDao {
    @Query("SELECT * FROM deals ORDER BY timestamp DESC")
    fun getAllDeals(): Flow<List<DealEntity>>

    @Query("SELECT * FROM deals WHERE opportunityId = :opId LIMIT 1")
    suspend fun getDealByOpportunityIdDirect(opId: Int): DealEntity?

    @Query("SELECT * FROM deals WHERE opportunityId = :opId LIMIT 1")
    fun getDealByOpportunityId(opId: Int): Flow<DealEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeal(deal: DealEntity): Long

    @Query("UPDATE deals SET status = :status, timestamp = :time WHERE id = :dealId")
    suspend fun updateDealStatus(dealId: Int, status: String, time: Long)
}
