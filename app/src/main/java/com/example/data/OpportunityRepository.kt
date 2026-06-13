package com.example.data

import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class SupabaseOpportunity(
    val id: Int,
    val title: String,
    @SerialName("brand_name") val brandName: String? = "Brand",
    @SerialName("budget_range") val budgetRange: String? = "",
    val type: String? = "Brand Deal",
    val platform: String? = "YouTube",
    val location: String? = "Remote",
    @SerialName("duration_text") val durationText: String? = "",
    val requirements: String? = "",
    @SerialName("about_campaign") val aboutCampaign: String? = "",
    @SerialName("commission_rate") val commissionRate: String? = "",
    @SerialName("difficulty_level") val difficultyLevel: String? = "",
    val category: String? = ""
)

class OpportunityRepository(private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)) {
    private val _opportunities = MutableStateFlow<List<OpportunityEntity>>(emptyList())
    val opportunities: StateFlow<List<OpportunityEntity>> = _opportunities.asStateFlow()

    init {
        fetchOpportunities()
    }

    private fun fetchOpportunities() {
        coroutineScope.launch {
            try {
                val supabaseOps = SupabaseManager.client.postgrest["opportunities"]
                    .select()
                    .decodeList<SupabaseOpportunity>()
                
                val domainOps = supabaseOps.map {
                    OpportunityEntity(
                        id = it.id,
                        title = it.title,
                        brandName = it.brandName ?: "",
                        budgetRange = it.budgetRange ?: "",
                        type = it.type ?: "",
                        platform = it.platform ?: "",
                        location = it.location ?: "",
                        durationText = it.durationText ?: "",
                        requirements = it.requirements ?: "",
                        aboutCampaign = it.aboutCampaign ?: "",
                        commissionRate = it.commissionRate ?: "",
                        difficultyLevel = it.difficultyLevel ?: "",
                        category = it.category ?: "",
                        isSaved = false
                    )
                }
                _opportunities.value = domainOps
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
