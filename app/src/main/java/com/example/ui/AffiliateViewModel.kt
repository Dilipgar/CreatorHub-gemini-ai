package com.example.ui

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.data.AffiliateEarningEntity
import com.example.data.AffiliateOfferEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AffiliateViewModel(
    private val parent: CreatorHubViewModel,
    private val application: Application
) {
    val selectedAffiliateOfferId = MutableStateFlow<Int?>(null)

    val selectedAffiliateOffer: StateFlow<AffiliateOfferEntity?> = selectedAffiliateOfferId
        .flatMapLatest { id ->
            if (id != null) parent.repository.getAffiliateOffer(id) else flowOf(null)
        }.stateIn(parent.viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allAffiliateOffers: StateFlow<List<AffiliateOfferEntity>> = parent.repository.affiliateOffers
        .stateIn(parent.viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val affiliateEarnings: StateFlow<List<AffiliateEarningEntity>> = parent.repository.affiliateEarnings
        .stateIn(parent.viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val affiliateSearchQuery = MutableStateFlow("")
    val affiliateCategoryFilter = MutableStateFlow("All") // "All", "SaaS & Hosting", "Design & Creative", "E-commerce & Retail", "Education & Self-Care"

    val filteredAffiliateOffers: StateFlow<List<AffiliateOfferEntity>> = combine(
        allAffiliateOffers,
        affiliateSearchQuery,
        affiliateCategoryFilter
    ) { offers, query, cat ->
        offers.filter { offer ->
            val matchesQuery = offer.title.contains(query, ignoreCase = true) ||
                    offer.brandName.contains(query, ignoreCase = true)
            val matchesCategory = if (cat == "All") true else offer.category == cat
            matchesQuery && matchesCategory
        }
    }.stateIn(parent.viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectAffiliateOffer(id: Int) {
        selectedAffiliateOfferId.value = id
        parent.authViewModel.navigateTo("affiliate_details")
    }

    fun setAffiliateSearchQuery(query: String) {
        affiliateSearchQuery.value = query
    }

    fun setAffiliateCategoryFilter(category: String) {
        affiliateCategoryFilter.value = category
    }

    fun toggleSaveAffiliateOffer(id: Int) {
        parent.viewModelScope.launch {
            parent.repository.toggleSaveAffiliateOffer(id)
        }
    }

    fun applyToAffiliateOffer(id: Int, brandName: String) {
        parent.viewModelScope.launch {
            val handleClean = parent.profileViewModel.userProfile.value?.handle?.removePrefix("@") ?: "ankitclicks"
            val brandClean = brandName.lowercase().replace(" ", "")
            val trackingLink = "https://creatorhub.link/ref/$handleClean/$brandClean"
            parent.repository.applyToAffiliateOffer(id, trackingLink)

            // Start an interactive chat introduction with the affiliate support manager
            val welcomeMsg = "Congratulations! Your affiliate partner request for $brandName has been auto-approved. Your verified status: ELITE PARTNER. Use your custom tracking link inside your bio and posts to maximize commission earnings immediately."
            parent.repository.startChatSession("$brandName Affiliate", "YouTube", welcomeMsg)
        }
    }

    fun simulateClickOnAffiliate(offerId: Int) {
        parent.viewModelScope.launch {
            val earningsList = affiliateEarnings.value
            val activeEarning = earningsList.find { it.offerId == offerId }
            if (activeEarning != null) {
                val nextClicks = activeEarning.clicksCount + 1
                val isConversion = (1..100).random() <= 8 // 8% conversion rate
                
                var nextConversions = activeEarning.conversionsCount
                var nextSales = activeEarning.totalSales
                var nextEarnings = activeEarning.earningsAmount
                
                if (isConversion) {
                    nextConversions += 1
                    
                    // Sales amount generated ranges between 1000 and 5000
                    val saleVal = (1000..5000).random().toDouble()
                    nextSales += saleVal
                    
                    // Earned commission based on rate or flat
                    val commissionRateText = allAffiliateOffers.value.find { it.id == offerId }?.commissionRate ?: "10%"
                    val computedEarning = if (commissionRateText.contains("₹")) {
                        commissionRateText.filter { it.isDigit() }.toDoubleOrNull() ?: 500.0
                    } else {
                        val percentage = commissionRateText.filter { it.isDigit() }.toDoubleOrNull() ?: 10.0
                        (saleVal * percentage) / 100.0
                    }
                    nextEarnings += computedEarning
                }

                parent.repository.insertAffiliateEarning(
                    activeEarning.copy(
                        clicksCount = nextClicks,
                        conversionsCount = nextConversions,
                        totalSales = nextSales,
                        earningsAmount = nextEarnings,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    }
}
